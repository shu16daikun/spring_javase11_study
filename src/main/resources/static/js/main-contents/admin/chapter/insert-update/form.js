// /js/main-contents/admin/chapter/insert-update/form.js
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";
import { CONST } from "./const.js";

const LOG = getLoger("admin.chapter.insert-update.form");

export class ChapterInsertUpdateForm {
	constructor($rootForm) {
		const span = LOG.logStart("ChapterInsertUpdateForm#constructor", { level: "TRACE", duration: false });

		this.$form = $rootForm || $(CONST.SELECTOR.FORM);
		this.$no = this.$form.find(CONST.SELECTOR.NO);
		this.$title = this.$form.find(CONST.SELECTOR.TITLE);
		this.$book = this.$form.find(CONST.SELECTOR.BOOK_SELECT);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();

		// A11y: 入力とエラーを関連付け
		const $invNo = this.$no.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invNo.length) {
			const id = $invNo.attr("id") || "no-error";
			$invNo.attr("id", id);
			this.$no.attr("aria-describedby", id);
		}
		const $invTitle = this.$title.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invTitle.length) {
			const id2 = $invTitle.attr("id") || "title-error";
			$invTitle.attr("id", id2);
			this.$title.attr("aria-describedby", id2);
		}
		const $invBook = this.$book.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($invBook.length) {
			const id3 = $invBook.attr("id") || "book-error";
			$invBook.attr("id", id3);
			this.$book.attr("aria-describedby", id3);
		}

		span.end();
	}

	getValues() {
		const span = LOG.logStart("ChapterInsertUpdateForm#getValues", { level: "TRACE", duration: false });

		const v = {
			no: (this.$no.val() || "").trim(),
			title: (this.$title.val() || "").trim(),
			sankouBooksViewId: (this.$book.val() || "").trim(),
		};

		return endAndReturn(span, v);
	}

	reset() {
		const span = LOG.logStart("ChapterInsertUpdateForm#reset", { level: "DEBUG", duration: false });

		this.$no.val("");
		this.$title.val("");
		this.$book.val("");
		this.hideErrorMessage();
		this.clearErrors();

		span.end();
	}

	clearErrors() {
		const span = LOG.logStart("ChapterInsertUpdateForm#clearErrors", { level: "DEBUG", duration: false });

		const $allC = this.$form.find(CONST.SELECTOR.CONTAINER);
		const $allInv = this.$form.find(CONST.SELECTOR.INVALID);
		$allC.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		$allInv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		this.$no.attr(CONST.ATTR.ARIA_INVALID, "false");
		this.$title.attr(CONST.ATTR.ARIA_INVALID, "false");
		this.$book.attr(CONST.ATTR.ARIA_INVALID, "false");

		span.end();
	}

	showErrorMessage(message) {
		const span = LOG.logStart("ChapterInsertUpdateForm#showErrorMessage", { level: "DEBUG", duration: false });

		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.attr(CONST.ATTR.DATA_TEXT, message).text(message).stop(true, true).fadeIn(120);

		span.end();
	}

	hideErrorMessage() {
		const span = LOG.logStart("ChapterInsertUpdateForm#hideErrorMessage", { level: "TRACE", duration: false });

		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();
		this.$errorAlert.stop(true, true).fadeOut(100, function() {
			$(this).text("").attr(CONST.ATTR.DATA_TEXT, "").hide();
		});

		span.end();
	}

	showNoFieldError(message) {
		const span = LOG.logStart("ChapterInsertUpdateForm#showNoFieldError", { level: "TRACE", duration: false });

		const $c = this.$no.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$no.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });

		span.end();
	}

	showNoFieldValid() {
		const span = LOG.logStart("ChapterInsertUpdateForm#showNoFieldValid", { level: "TRACE", duration: false });

		const $c = this.$no.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$no.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		span.end();
	}

	showTitleFieldError(message) {
		const span = LOG.logStart("ChapterInsertUpdateForm#showTitleFieldError", { level: "TRACE", duration: false });

		const $c = this.$title.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$title.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });

		span.end();
	}

	showTitleFieldValid() {
		const span = LOG.logStart("ChapterInsertUpdateForm#showTitleFieldValid", { level: "TRACE", duration: false });

		const $c = this.$title.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$title.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		span.end();
	}

	showBookFieldError(message) {
		const span = LOG.logStart("ChapterInsertUpdateForm#showBookFieldError", { level: "TRACE", duration: false });

		const $c = this.$book.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		this.$book.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });

		span.end();
	}

	showBookFieldValid() {
		const span = LOG.logStart("ChapterInsertUpdateForm#showBookFieldValid", { level: "TRACE", duration: false });

		const $c = this.$book.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		this.$book.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		span.end();
	}

	focusFirstError(errors) {
		const span = LOG.logStart("ChapterInsertUpdateForm#focusFirstError", { level: "TRACE", duration: false });

		if (errors.no) {
			this.$no.trigger("focus");
			span.end();
			return;
		}
		if (errors.title) {
			this.$title.trigger("focus");
			span.end();
			return;
		}
		if (errors.sankouBooksViewId) {
			this.$book.trigger("focus");
		}

		span.end();
	}
}
