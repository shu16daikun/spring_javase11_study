// /js/main-contents/admin/authority/insert-update/form.js
import { CONST } from "./const.js";
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.authority.insertUpdate.form");

export class AuthorityInsertUpdateForm {
	constructor($rootForm) {
		const span = LOG.logStart("AuthorityInsertUpdateForm#constructor", { level: "TRACE", duration: false });

		this.$form = $rootForm || $(CONST.SELECTOR.FORM);
		this.$name = this.$form.find(CONST.SELECTOR.NAME);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();

		// A11y: 入力とエラーの関連付け
		const $invalid = this.$name.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invalid.length) {
			const id = $invalid.attr("id") || "systemName-error";
			$invalid.attr("id", id);
			this.$name.attr("aria-describedby", id);
		}

		span.end();
	}

	getValues() {
		const span = LOG.logStart("AuthorityInsertUpdateForm#getValues", { level: "TRACE", duration: false });
		const v = { name: (this.$name.val() || "").trim() };
		return endAndReturn(span, v);
	}

	reset() {
		const span = LOG.logStart("AuthorityInsertUpdateForm#reset", { level: "TRACE", duration: false });

		this.$name.val("");
		this.hideErrorMessage();
		this.clearErrors();

		span.end();
	}

	clearErrors() {
		const span = LOG.logStart("AuthorityInsertUpdateForm#clearErrors", { level: "TRACE", duration: false });

		const $c = this.$name.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		this.$name.attr(CONST.ATTR.ARIA_INVALID, "false");
		// 高さ維持（NBSP + visibility:hidden）
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		span.end();
	}

	showErrorMessage(message) {
		const span = LOG.logStart("AuthorityInsertUpdateForm#showErrorMessage", { level: "TRACE", duration: false });

		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, message).text(message).stop(true, true).fadeIn(120);

		span.end();
	}

	hideErrorMessage() {
		const span = LOG.logStart("AuthorityInsertUpdateForm#hideErrorMessage", { level: "TRACE", duration: false });

		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.stop(true, true).fadeOut(100, function() {
			$(this).text("").attr(CONST.ATTR.DATA_TEXT, "").hide();
		});

		span.end();
	}

	showNameFieldError(message) {
		const span = LOG.logStart("AuthorityInsertUpdateForm#showNameFieldError", { level: "TRACE", duration: false });

		const $c = this.$name.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$name.attr(CONST.ATTR.ARIA_INVALID, "true");
		// 表示しつつレイアウト確保（visibility:visible）
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });

		span.end();
	}

	showNameFieldValid() {
		const span = LOG.logStart("AuthorityInsertUpdateForm#showNameFieldValid", { level: "TRACE", duration: false });

		const $c = this.$name.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$name.attr(CONST.ATTR.ARIA_INVALID, "false");
		// 文言は空にして高さ維持（NBSP + hidden）
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		span.end();
	}

	focusFirstError(errors) {
		const span = LOG.logStart("AuthorityInsertUpdateForm#focusFirstError", { level: "TRACE", duration: false });

		if (errors.systemName) this.$name.trigger("focus");

		span.end();
	}
}
