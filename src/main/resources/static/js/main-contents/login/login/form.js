// /js/main-contents/login/form.js
// 機能：ログインフォーム操作
import { CONST } from "./const.js";

export class LoginForm {
	constructor($root) {
		this.$form = $root || $(CONST.SELECTOR.FORM);
		this.$username = this.$form.find(CONST.SELECTOR.USERNAME);
		this.$password = this.$form.find(CONST.SELECTOR.PASSWORD);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR);
	}

	getValues() {
		return {
			username: this.$username.val(),
			password: this.$password.val(),
		};
	}

	clear() {
		this.$username.val("");
		this.$password.val("");
		this.hideErrorMessage();
		this.clearErrors();
	}
	reset() { this.clear(); }

	clearErrors() {
		this.$form.find(CONST.SELECTOR.INVALID).text("");
		this.$form.find(CONST.SELECTOR.CONTAINER)
			.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		this.$username.attr(CONST.ATTR.ARIA_INVALID, "false");
		this.$password.attr(CONST.ATTR.ARIA_INVALID, "false");
	}

	hideErrorMessage() {
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, "").text("").hide();
	}

	showErrorMessage(message) {
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, message).text(message).fadeIn(200);
	}

	showFieldError($input, message) {
		const $container = $input.closest(CONST.SELECTOR.CONTAINER);
		$container.addClass(CONST.CLASS.IS_INVALID);
		$container.find(CONST.SELECTOR.INVALID).text(message);
		$input.attr(CONST.ATTR.ARIA_INVALID, "true");
	}

	showUsernameFieldError(message) { this.showFieldError(this.$username, message); }
	showPasswordFieldError(message) { this.showFieldError(this.$password, message); }

	focusFirstError(errors) {
		if (errors.username) { this.$username.trigger("focus"); return; }
		if (errors.password) { this.$password.trigger("focus"); return; }
	}
}
