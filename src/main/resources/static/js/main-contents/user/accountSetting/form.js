// /js/main-contents/user/account_setting/form.js
/* 機能：アカウント設定 フォーム操作 */
import { CONST } from "./const.js";

export class AccountSettingForm {
	constructor($form) {
		this.$form = $form || $(CONST.SELECTOR.FORM);
		this.$username = this.$form.find(CONST.SELECTOR.USERNAME);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();

		// A11y: 入力とエラーの関連付け（存在すれば属性だけ付与）
		const $invalid = this.$username.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invalid.length) {
			const id = $invalid.attr("id") || "username-error";
			$invalid.attr("id", id);
			this.$username.attr("aria-describedby", id);
		}
	}

	getValues() {
		return { username: (this.$username.val() || "").trim() };
	}

	reset() {
		this.$username.val("");
		this.hideErrorMessage();
		this.clearErrors();
	}

	clearErrors() {
		this.$form.find(CONST.SELECTOR.INVALID).text("").attr(CONST.ATTR.DATA_TEXT, "");
		this.$form.find(CONST.SELECTOR.CONTAINER)
			.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		this.$username.attr(CONST.ATTR.ARIA_INVALID, "false");
	}

	showErrorMessage(message) {
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, message).text(message).stop(true, true).fadeIn(120);
	}

	hideErrorMessage() {
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.stop(true, true).fadeOut(100, function() {
			$(this).text("").attr(CONST.ATTR.DATA_TEXT, "").hide();
		});
	}

	showUsernameFieldError(message) {
		const $c = this.$username.closest(CONST.SELECTOR.CONTAINER);
		$c.addClass(CONST.CLASS.IS_INVALID);
		$c.find(CONST.SELECTOR.INVALID).attr(CONST.ATTR.DATA_TEXT, message).text(message);
		this.$username.attr(CONST.ATTR.ARIA_INVALID, "true");
	}

	focusFirstError(errors) {
		if (errors.username) this.$username.trigger("focus");
	}
}
