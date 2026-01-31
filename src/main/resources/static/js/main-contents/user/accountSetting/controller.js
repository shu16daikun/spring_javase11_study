// /js/main-contents/user/account_setting/controller.js
/* 機能：アカウント設定 画面エントリポイント */
import { setValidationInputChange, setValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { appendCsrfToForm } from "/js/util/csrf.js";
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { AccountSettingForm } from "./form.js";
import { AccountSettingService } from "./service.js";

/* 共通初期化 */
setCommonReady(() => {
	const LOG = getLoger("USER_ACCOUNT_SETTING");

	// 初期UI
	setFormFloatLabel();
	setAlertDanger();

	const form = new AccountSettingForm();
	const service = new AccountSettingService(CONST.MESSAGES());

	/* 送信計測：ページ遷移を跨ぐので sessionStorage */
	const formId = form.$form.attr("id") || CONST.LOG.FORM_ID_FALLBACK;
	const SS_KEY = `psfm:submitAt:${formId}`;

	const markSend = () => {
		try {
			sessionStorage.setItem(SS_KEY, String(Date.now()));
		} catch (_e) {
			/* noop */
		}
	};

	const consumeSendAt = () => {
		try {
			const v = sessionStorage.getItem(SS_KEY);
			if (!v) return null;
			sessionStorage.removeItem(SS_KEY);
			const n = Number(v);
			return Number.isFinite(n) ? n : null;
		} catch (_e) {
			return null;
		}
	};

	/* 初期アラートが空なら非表示 */
	const initMsg = (form.$errorAlert.attr(CONST.ATTR.DATA_TEXT) || "").trim();
	if (!initMsg) form.hideErrorMessage();

	/* 入力中バリデーション（即時UX） */
	setValidationInputChange(
		(values) => {
			const errors = service.validate(values) || {};
			setValidation(errors, CONST.VALIDATION_TARGETS);

			form.clearErrors();

			if (errors.username) {
				form.showUsernameFieldError(errors.username);

				// 旧: L.validation(...)
				LOG.debug(
					"validation field={0} ok={1} ruleKey={2} errKey={3} len={4}",
					CONST.LOG.FIELD.USERNAME,
					false,
					service.REGEX?.USERNAME || "",
					CONST.LOG.ERR_KEY.USERNAME_PATTERN,
					(values?.username || "").length
				);
			} else {
				form.hideErrorMessage();

				LOG.debug(
					"validation field={0} ok={1} ruleKey={2}",
					CONST.LOG.FIELD.USERNAME,
					true,
					service.REGEX?.USERNAME || ""
				);
			}
			return errors;
		},
		{ getTargetsForAlert: () => CONST.VALIDATION_TARGETS }
	);

	/* 送信（エラー時のみ中断） */
	form.$form.off(CONST.EVENT.SUBMIT_NS).on(CONST.EVENT.SUBMIT_NS, function(e) {
		const span = LOG.logStart(`submit ${formId}`, { level: "INFO", duration: true });

		const values = form.getValues();
		const errors = service.validate(values) || {};
		setValidation(errors, CONST.VALIDATION_TARGETS);
		form.clearErrors();

		if (errors.username) {
			form.showUsernameFieldError(errors.username);
			e.preventDefault();
			form.focusFirstError(errors);

			// 旧: L.submitAttempt(...)
			LOG.warn("submitAttempt formId={0} ok=false reasons={1}", formId, "validation_failed");

			span.end();
			return;
		}

		// 通過：CSRF 付与してネイティブ submit
		appendCsrfToForm(form.$form[0]);
		LOG.info("submitAttempt formId={0} ok=true", formId);
		markSend();

		span.end();
		// ネイティブ遷移に任せる
	});

	/* 応答戻りの簡易計測 */
	window.addEventListener(
		"pageshow",
		() => {
			const t0 = consumeSendAt();
			if (t0 != null) {
				const elapsed = Math.max(0, Date.now() - t0);
				LOG.info("submitResult formId={0} status={1} ok={2} elapsedMs={3}", formId, 200, true, elapsed);
			}
		},
		{ once: true }
	);
});
