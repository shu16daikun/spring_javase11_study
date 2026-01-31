// /js/main-contents/login/password_set/controller.js
/* 機能：パスワード設定 エントリポイント */
import { setValidationInputChange, setValidation as applyValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { appendCsrfToForm } from "/js/util/csrf.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { PasswordSetForm } from "./form.js";
import { PasswordSetService } from "./service.js";
import { setCommonReady } from "../common.js";

setCommonReady(() => {
	const LOG = getLoger("PASSWORD_SET");

	/* ① .my-invalid が無ければ補完（先にやる） */
	(function ensureInvalidBlocks() {
		$(CONST.SELECTOR.FORM + " " + CONST.SELECTOR.CONTAINER).each(function() {
			if ($(this).find(CONST.SELECTOR.INVALID).length === 0) {
				$(this).append('<div class="my-invalid" data-text=""></div>');
			}
		});
	})();

	/* 初期化 */
	setFormFloatLabel();
	setAlertDanger();

	const form = new PasswordSetForm();
	const service = new PasswordSetService(CONST.MESSAGES());

	/* 送信計測（ページ遷移を跨ぐ可能性があるので sessionStorage） */
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

	/* 入力中バリデーション */
	setValidationInputChange(
		(values) => {
			const errors = service.validate(values) || {};

			applyValidation(errors, CONST.VALIDATION_TARGETS);

			if (errors.global) form.showErrorMessage(errors.global);
			else form.hideErrorMessage();

			// アラート可視状態が変わった後に再評価
			applyValidation(errors, CONST.VALIDATION_TARGETS);

			// log（旧 L.validation 相当）
			const F = CONST.LOG.FIELD;
			const EK = CONST.LOG.ERR_KEY;

			if ("password" in errors) LOG.debug("validation {0} ok={1} errKey={2}", F.PASSWORD, false, EK.PASSWORD_ANY);
			else if (values?.password != null) LOG.debug("validation {0} ok={1} ruleKey={2}", F.PASSWORD, true, service?.REGEX?.PASSWORD || "");

			if ("passwordCheck" in errors) LOG.debug("validation {0} ok={1} errKey={2}", F.PASSWORD_CHECK, false, EK.PASSWORD_MISMATCH);
			else if (values?.passwordCheck != null) LOG.debug("validation {0} ok={1}", F.PASSWORD_CHECK, true);

			if ("global" in errors) LOG.debug("validation {0} ok={1} errKey={2}", F.GLOBAL, false, EK.PASSWORD_PATTERN);

			return errors;
		},
		{ getTargetsForAlert: () => CONST.VALIDATION_TARGETS }
	);

	/* 送信時（名前空間付き） */
	form.$form.off(CONST.EVENT.SUBMIT_NS).on(CONST.EVENT.SUBMIT_NS, function(e) {
		e.preventDefault();
		form.clearErrors();

		const span = LOG.logStart(`submit ${formId}`, { level: "INFO", duration: true });

		const values = form.getValues();
		const errors = service.validate(values) || {};

		applyValidation(errors, CONST.VALIDATION_TARGETS);

		if (errors.password) form.showPasswordFieldError(errors.password);
		if (errors.passwordCheck) form.showPasswordCheckFieldError(errors.passwordCheck);

		if (errors.global) form.showErrorMessage(errors.global);
		else form.hideErrorMessage();

		// アラート可視→ invalid 固定
		applyValidation(errors, CONST.VALIDATION_TARGETS);

		if (Object.keys(errors).length > 0) {
			LOG.warn("submitAttempt formId={0} ok=false reasons={1}", formId, "validation_failed");
			span.end();
			form.focusFirstError(errors);
			return;
		}

		appendCsrfToForm(form.$form[0]);

		LOG.info("submitAttempt formId={0} ok=true", formId);
		markSend();

		span.end();
		form.$form.off(CONST.EVENT.SUBMIT_NS);
		form.$form[0].submit();
	});

	/* 応答戻り */
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
