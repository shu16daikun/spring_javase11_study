import { CONST } from "./const.js";

export class KurohonQuestionsInsertUpdateForm {
	constructor($rootForm) {
		this.$form = $rootForm || $(CONST.SELECTOR.FORM);
		this.$book = this.$form.find(CONST.SELECTOR.BOOK_SELECT);
		this.$chapter = this.$form.find(CONST.SELECTOR.CHAPTER_SELECT);
		this.$qno = this.$form.find(CONST.SELECTOR.QUESTION_NO);
		this.$correct = this.$form.find(CONST.SELECTOR.CORRECT_OPTION);
		this.$ansMax = this.$form.find(CONST.SELECTOR.ANSWER_COUNT_MAX);
		this.$optCnt = this.$form.find(CONST.SELECTOR.OPTION_COUNT);
		this.$qhtml = this.$form.find(CONST.SELECTOR.QUESTION_HTML);
		this.$exhtml = this.$form.find(CONST.SELECTOR.EXPLANATION_HTML);
		this.$errorAlert = $(CONST.SELECTOR.ALERT_ERROR).first();

		// A11y: aria-describedby
		this._bindA11y(this.$book, "book-error");
		this._bindA11y(this.$chapter, "chapter-error");
		this._bindA11y(this.$qno, "qno-error");
		this._bindA11y(this.$correct, "correct-error");
		this._bindA11y(this.$ansMax, "answermax-error");
		this._bindA11y(this.$optCnt, "optioncount-error");
	}

	_bindA11y($input, fallbackId) {
		const $inv = $input.closest(CONST.SELECTOR.CONTAINER).find(CONST.SELECTOR.INVALID);
		if ($inv.length) {
			const id = $inv.attr("id") || fallbackId;
			$inv.attr("id", id);
			$input.attr("aria-describedby", id);
		}
	}

	getValues() {
		return {
			sankouBooksViewId: (this.$book.val() || "").trim(),
			chapterViewId: (this.$chapter.val() || "").trim(),
			questionNo: (this.$qno.val() || "").trim(),
			correctOption: (this.$correct.val() || "").trim(),
			answerCountMax: (this.$ansMax.val() || "").trim(),
			optionCount: (this.$optCnt.val() || "").trim(),
			questionHtml: (this.$qhtml.val() || "").trim(),
			explanationHtml: (this.$exhtml.val() || "").trim(),
		};
	}

	reset() {
		this.$book.val("");
		this.$chapter.val("");
		this.$qno.val("");
		this.$correct.val("");
		this.$ansMax.val("");
		this.$optCnt.val("");
		this.$qhtml.val("");
		this.$exhtml.val("");
		this.hideErrorMessage();
		this.clearErrors();
	}

	clearErrors() {
		const $allC = this.$form.find(CONST.SELECTOR.CONTAINER);
		const $allInv = this.$form.find(CONST.SELECTOR.INVALID);
		$allC.removeClass(`${CONST.CLASS.IS_INVALID} ${CONST.CLASS.IS_VALID}`);
		$allInv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });

		[this.$book, this.$chapter, this.$qno, this.$correct, this.$ansMax, this.$optCnt].forEach(($el) => {
			$el.attr(CONST.ATTR.ARIA_INVALID, "false");
		});
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

	// --- per-field ---
	_showFieldError($input, message) {
		const $c = $input.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_VALID).addClass(CONST.CLASS.IS_INVALID);
		$input.attr(CONST.ATTR.ARIA_INVALID, "true");
		$inv.attr(CONST.ATTR.DATA_TEXT, message).text(String(message)).css({ visibility: "visible" });
	}
	_showFieldValid($input) {
		const $c = $input.closest(CONST.SELECTOR.CONTAINER);
		const $inv = $c.find(CONST.SELECTOR.INVALID);
		$c.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		$input.attr(CONST.ATTR.ARIA_INVALID, "false");
		$inv.attr(CONST.ATTR.DATA_TEXT, "").text("\u00A0").css({ visibility: "hidden" });
	}

	showBookFieldError(m) { this._showFieldError(this.$book, m); }
	showBookFieldValid() { this._showFieldValid(this.$book); }

	showChapterFieldError(m) { this._showFieldError(this.$chapter, m); }
	showChapterFieldValid() { this._showFieldValid(this.$chapter); }

	showQuestionNoFieldError(m) { this._showFieldError(this.$qno, m); }
	showQuestionNoFieldValid() { this._showFieldValid(this.$qno); }

	showCorrectFieldError(m) { this._showFieldError(this.$correct, m); }
	showCorrectFieldValid() { this._showFieldValid(this.$correct); }

	showAnswerMaxFieldError(m) { this._showFieldError(this.$ansMax, m); }
	showAnswerMaxFieldValid() { this._showFieldValid(this.$ansMax); }

	showOptionCountFieldError(m) { this._showFieldError(this.$optCnt, m); }
	showOptionCountFieldValid() { this._showFieldValid(this.$optCnt); }

	focusFirstError(errors) {
		if (errors.sankouBooksViewId) { this.$book.trigger("focus"); return; }
		if (errors.chapterViewId) { this.$chapter.trigger("focus"); return; }
		if (errors.questionNo) { this.$qno.trigger("focus"); return; }
		if (errors.correctOption) { this.$correct.trigger("focus"); return; }
		if (errors.answerCountMax) { this.$ansMax.trigger("focus"); return; }
		if (errors.optionCount) { this.$optCnt.trigger("focus"); return; }
	}
}
