// /js/main-contents/login/controller.js
/* 機能：ログイン画面エントリポイント */
import { setValidation as applyValidation } from "/psfm/js/fragment/validation.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { LoginForm } from "./form.js";
import { LoginService } from "./service.js";
import { setCommonReady } from "../common.js";

setCommonReady(() => {
	const LOG = getLoger("LOGIN");

	/* 依存生成 */
	const $form = $(CONST.SELECTOR.FORM);
	const form = new LoginForm($form);
	const service = new LoginService(CONST.MESSAGES());

	/* 画面初期化 */
	setFormFloatLabel();
	setAlertDanger();
	form.clearErrors();

	/* 送信計測：ページ遷移を跨ぐので sessionStorage に置く */
	const formId = $form.attr("id") || CONST.LOG.FORM_ID_FALLBACK;
	const SS_KEY = `psfm:submitAt:${formId}`;

	const markSend = () => {
		try {
			sessionStorage.setItem(SS_KEY, String(Date.now()));
		} catch (_e) {
			// storage無理なら諦める（ログ目的なので握りつぶし）
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

	/* 送信ハンドラ（名前空間付きで二重バインド防止） */
	$form.off(CONST.EVENT.SUBMIT_NS).on(CONST.EVENT.SUBMIT_NS, (e) => {
		e.preventDefault();

		const span = LOG.logStart(`submit ${formId}`, { level: "INFO", duration: true });

		// 入力値
		const values = form.getValues();

		// 検証
		const errors = service.validate(values) || {};

		// UI反映
		applyValidation(errors);
		form.clearErrors();
		if (errors.username) form.showUsernameFieldError(errors.username);
		if (errors.password) form.showPasswordFieldError(errors.password);
		if (errors.global) form.showErrorMessage(errors.global);

		// ログ（validation）: 旧 L.validation を置換
		const F = CONST.LOG.FIELD;
		const EK = CONST.LOG.ERR_KEY;

		if ("username" in errors) LOG.debug("validation {0} ok={1} errKey={2}", F.USERNAME, false, EK.USERNAME);
		else if (values?.username != null) LOG.debug("validation {0} ok={1}", F.USERNAME, true);

		if ("password" in errors) LOG.debug("validation {0} ok={1} errKey={2}", F.PASSWORD, false, EK.PASSWORD);
		else if (values?.password != null) LOG.debug("validation {0} ok={1}", F.PASSWORD, true);

		if ("global" in errors) LOG.debug("validation {0} ok={1} errKey={2}", F.GLOBAL, false, EK.MISMATCH);

		// エラー時はフォーカスして中断
		if (Object.keys(errors).length > 0) {
			LOG.warn("submitAttempt formId={0} ok=false reasons={1}", formId, "validation_failed");
			span.end();
			form.focusFirstError(errors);
			return;
		}

		// 通過：計測→ハンドラ解除してネイティブ送信
		LOG.info("submitAttempt formId={0} ok=true", formId);
		markSend();

		span.end();
		$form.off(CONST.EVENT.SUBMIT_NS);
		$form[0].submit();
	});

	/* 応答戻り（同ページ再表示など） */
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
