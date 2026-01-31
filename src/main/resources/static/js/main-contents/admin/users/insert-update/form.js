// /js/main-contents/admin/users/insert-update/form.js
import { CONST } from "./const.js";

export class UsersInsertUpdateForm {
	constructor($rootForm) {
		this.$form = $rootForm || $(CONST.SELECTOR.FORM);
		this.$username = this.$form.find(CONST.SELECTOR.USERNAME);
		this.$authSelect = this.$form.find(CONST.SELECTOR.AUTH_SELECT);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();

		// A11y: 入力とエラーを関連付け
		const $invalidUser = this.$username.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invalidUser.length) {
			const id = $invalidUser.attr("id") || "username-error";
			$invalidUser.attr("id", id);
			this.$username.attr("aria-describedby", id);
		}
		const $invalidAuth = this.$authSelect.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invalidAuth.length) {
			const id2 = $invalidAuth.attr("id") || "authority-error";
			$invalidAuth.attr("id", id2);
			this.$authSelect.attr("aria-describedby", id2);
		}
	}

	getValues() {
		return {
			username: (this.$username.val() || "").trim(),
			authorityViewId: (this.$authSelect.val() || "").trim(),
		};
	}

	reset() {
		this.$username.val("");
		this.$authSelect.val("");
		this.hideErrorMessage();
		this.clearErrors();
	}

	clearErrors() {
		const $allC = this.$form.find(CONST.SELECTOR.CONTAINER);
		const $allInv = this.$form.find(CONST.SELECTOR.INVALID);

		$allC.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		$allInv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		this.$username.attr(CONST.ATTR.ARIA_INVALID, "false");
		this.$authSelect.attr(CONST.ATTR.ARIA_INVALID, "false");
	}

	showErrorMessage(message) {
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert
			.attr(CONST.ATTR.DATA_TEXT, message)
			.text(message)
			.stop(true, true)
			.fadeIn(120);
	}

	hideErrorMessage() {
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.stop(true, true).fadeOut(100, function() {
			$(this).text("").attr(CONST.ATTR.DATA_TEXT, "").hide();
		});
	}

	showUsernameFieldError(message) {
		const $c = this.$username.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$username.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });
	}

	showUsernameFieldValid() {
		const $c = this.$username.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$username.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });
	}

	showAuthorityFieldError(message) {
		const $c = this.$authSelect.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$authSelect.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });
	}

	showAuthorityFieldValid() {
		const $c = this.$authSelect.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$authSelect.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });
	}

	focusFirstError(errors) {
		if (errors.username) {
			this.$username.trigger("focus");
			return;
		}
		if (errors.authorityViewId) {
			this.$authSelect.trigger("focus");
		}
	}
}
