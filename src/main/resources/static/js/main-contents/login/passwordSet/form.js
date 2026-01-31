// /js/main-contents/login/password_set/form.js
/* 機能：パスワード設定 フォーム操作 */
import { CONST } from "./const.js";

export class PasswordSetForm {
	constructor($form) {
		this.$form = $form || $(CONST.SELECTOR.FORM);
		this.$password = this.$form.find(CONST.SELECTOR.PASSWORD);
		this.$passwordCheck = this.$form.find(CONST.SELECTOR.PASSWORD_CHECK);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first(); // 常に最新を拾う
	}

	getValues() {
		return {
			password: this.$password.val(),
			passwordCheck: this.$passwordCheck.val(),
		};
	}

	clearErrors() {
		this.$form.find(CONST.SELECTOR.INVALID).text("").attr(CONST.ATTR.DATA_TEXT, "");
		this.$form.find(CONST.SELECTOR.CONTAINER).removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		this.$password.attr(CONST.ATTR.ARIA_INVALID, "false");
		this.$passwordCheck.attr(CONST.ATTR.ARIA_INVALID, "false");
	}

	showErrorMessage(message) {
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, message).text(message).show();
	}
	hideErrorMessage() {
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, "").text("").hide();
	}

	showPasswordFieldError(message) {
		const $c = this.$password.closest(CONST.SELECTOR.CONTAINER);
		$c.addClass(CONST.CLASS.IS_INVALID);
		$c.find(CONST.SELECTOR.INVALID).attr(CONST.ATTR.DATA_TEXT, message).text(message);
		this.$password.attr(CONST.ATTR.ARIA_INVALID, "true");
	}

	showPasswordCheckFieldError(message) {
		const $c = this.$passwordCheck.closest(CONST.SELECTOR.CONTAINER);
		$c.addClass(CONST.CLASS.IS_INVALID);
		$c.find(CONST.SELECTOR.INVALID).attr(CONST.ATTR.DATA_TEXT, message).text(message);
		this.$passwordCheck.attr(CONST.ATTR.ARIA_INVALID, "true");
	}

	focusFirstError(errors) {
		if (errors.password) { this.$password.trigger("focus"); return; }
		if (errors.passwordCheck) { this.$passwordCheck.trigger("focus"); return; }
	}
}
