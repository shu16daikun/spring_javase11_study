// controller.js
/* 機能：黒本問題 追加/更新 画面エントリポイント */
import { setValidationInputChange, setValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { appendCsrfToForm } from "/js/util/csrf.js";
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { KurohonQuestionsInsertUpdateForm } from "./form.js";
import { KurohonQuestionsInsertUpdateService } from "./service.js";

const LOG = getLoger("admin.kurohonQuestions.insertUpdate");

/* 共通初期化（ここで ready を1回に統一） */
setCommonReady(() => {
	const initSpan = LOG.logStart("KurohonQuestionsInsertUpdateController#init", {
		level: "INFO",
		duration: true
	});

	/* 初期UI */
	setFormFloatLabel();
	setAlertDanger();

	const form = new KurohonQuestionsInsertUpdateForm($(CONST.SELECTOR.FORM));
	const service = new KurohonQuestionsInsertUpdateService(CONST.MESSAGES());
	const TARGETS = CONST.VALIDATION_TARGETS;

	/* 空の .my-invalid に行高確保 */
	form.$form.find(CONST.SELECTOR.INVALID).each(function() {
		const $el = $(this);
		const txt = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
		if (txt.length === 0) $el.text("\u00A0").css("visibility", "hidden");
	});

	/* 参考書変更 -> 章候補をフィルタ＆章を未選択に戻す */
	const filterChaptersByBook = (bookId) => {
		const span = LOG.logStart("KurohonQuestionsInsertUpdateController#filterChaptersByBook", {
			level: "TRACE",
			duration: true
		});

		const $opts = form.$chapter.find("option");
		$opts.each(function() {
			const $o = $(this);
			const val = String($o.val() || "");
			if (val === "") {
				// placeholder は常に表示
				$o.prop("disabled", false).show();
				return;
			}
			const b = String($o.attr(CONST.ATTR.DATA_BOOK) || "");
			if (!bookId || b !== bookId) {
				if ($o.is(":selected")) {
					form.$chapter.val(""); // 不整合を即解消
				}
				$o.prop("disabled", true).hide();
			} else {
				$o.prop("disabled", false).show();
			}
		});

		// 見た目更新 & 検証
		form.$chapter.trigger("change");

		span.end();
	};

	// 初回フィルタ（既定値がある場合に備える）
	filterChaptersByBook(String(form.$book.val() || "").trim());

	/* 参考書 select 変更 */
	$(document)
		.off(CONST.EVENT.CHANGE, CONST.SELECTOR.BOOK_SELECT)
		.on(CONST.EVENT.CHANGE, CONST.SELECTOR.BOOK_SELECT, () => {
			const span = LOG.logStart("KurohonQuestionsInsertUpdateController#onBookChange", {
				level: "DEBUG",
				duration: true
			});

			const bookId = String(form.$book.val() || "").trim();
			filterChaptersByBook(bookId);

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.sankouBooksViewId) {
				form.showErrorMessage(errors.sankouBooksViewId);
				form.showBookFieldError(errors.sankouBooksViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
				form.showBookFieldValid();
			}

			if (errors.chapterViewId) {
				form.showErrorMessage(errors.chapterViewId);
				form.showChapterFieldError(errors.chapterViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.showChapterFieldValid();
			}

			span.end();
		});

	/* 章 select 変更 */
	$(document)
		.off(CONST.EVENT.CHANGE, CONST.SELECTOR.CHAPTER_SELECT)
		.on(CONST.EVENT.CHANGE, CONST.SELECTOR.CHAPTER_SELECT, () => {
			const span = LOG.logStart("KurohonQuestionsInsertUpdateController#onChapterChange", {
				level: "DEBUG",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.chapterViewId) {
				form.showErrorMessage(errors.chapterViewId);
				form.showChapterFieldError(errors.chapterViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
				form.showChapterFieldValid();

				form.$form.find(CONST.SELECTOR.INVALID).each(function() {
					const $el = $(this);
					const t = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
					if (!t) $el.text("\u00A0").css("visibility", "hidden");
				});
			}

			span.end();
		});

	/* 入力中バリデーション */
	setValidationInputChange(
		(values) => {
			const span = LOG.logStart("KurohonQuestionsInsertUpdateController#onInputValidate", {
				level: "TRACE",
				duration: true
			});

			const v = {
				sankouBooksViewId: String(values["sankouBooksViewId"] ?? ""),
				chapterViewId: String(values["chapterViewId"] ?? ""),
				questionNo: String(values["questionNo"] ?? ""),
				correctOption: String(values["correctOption"] ?? ""),
				answerCountMax: String(values["answerCountMax"] ?? ""),
				optionCount: String(values["optionCount"] ?? "")
			};

			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			// per-field UI
			errors.sankouBooksViewId
				? form.showBookFieldError(errors.sankouBooksViewId)
				: form.showBookFieldValid();

			errors.chapterViewId
				? form.showChapterFieldError(errors.chapterViewId)
				: form.showChapterFieldValid();

			errors.questionNo
				? form.showQuestionNoFieldError(errors.questionNo)
				: form.showQuestionNoFieldValid();

			errors.correctOption
				? form.showCorrectFieldError(errors.correctOption)
				: form.showCorrectFieldValid();

			errors.answerCountMax
				? form.showAnswerMaxFieldError(errors.answerCountMax)
				: form.showAnswerMaxFieldValid();

			errors.optionCount
				? form.showOptionCountFieldError(errors.optionCount)
				: form.showOptionCountFieldValid();

			// 最初のエラーをヘッダに
			const firstMsg =
				errors.sankouBooksViewId ||
				errors.chapterViewId ||
				errors.questionNo ||
				errors.correctOption ||
				errors.answerCountMax ||
				errors.optionCount ||
				"";

			if (firstMsg) {
				form.showErrorMessage(firstMsg);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
			}

			// 空ならNBSP隠し
			form.$form.find(CONST.SELECTOR.INVALID).each(function() {
				const $el = $(this);
				const t = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
				if (!t) $el.text("\u00A0").css({ visibility: "hidden" });
			});

			span.end();
			return errors;
		},
		{ getTargetsForAlert: () => TARGETS }
	);

	/* モーダル前チェック */
	$(CONST.IDS.MODAL)
		.off("before:open.myModal.kurohonQuestions")
		.on("before:open.myModal.kurohonQuestions", (e) => {
			const span = LOG.logStart("KurohonQuestionsInsertUpdateController#beforeOpenModal", {
				level: "DEBUG",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg =
				errors.sankouBooksViewId ||
				errors.chapterViewId ||
				errors.questionNo ||
				errors.correctOption ||
				errors.answerCountMax ||
				errors.optionCount ||
				"";

			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.sankouBooksViewId) form.showBookFieldError(errors.sankouBooksViewId);
				if (errors.chapterViewId) form.showChapterFieldError(errors.chapterViewId);
				if (errors.questionNo) form.showQuestionNoFieldError(errors.questionNo);
				if (errors.correctOption) form.showCorrectFieldError(errors.correctOption);
				if (errors.answerCountMax) form.showAnswerMaxFieldError(errors.answerCountMax);
				if (errors.optionCount) form.showOptionCountFieldError(errors.optionCount);
				form.focusFirstError(errors);

				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showBookFieldValid();
			form.showChapterFieldValid();
			form.showQuestionNoFieldValid();
			form.showCorrectFieldValid();
			form.showAnswerMaxFieldValid();
			form.showOptionCountFieldValid();

			span.end();
			return true;
		});

	/* data-modal ボタン直押しの保険 */
	$(document)
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN, (e) => {
			const span = LOG.logStart("KurohonQuestionsInsertUpdateController#clickModalOpen", {
				level: "DEBUG",
				duration: true
			});

			const $btn = $(e.currentTarget);
			const target = String($btn.attr(CONST.ATTR.DATA_MODAL) || "");
			if (target !== CONST.IDS.MODAL) {
				span.end();
				return;
			}

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg =
				errors.sankouBooksViewId ||
				errors.chapterViewId ||
				errors.questionNo ||
				errors.correctOption ||
				errors.answerCountMax ||
				errors.optionCount ||
				"";

			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.sankouBooksViewId) form.showBookFieldError(errors.sankouBooksViewId);
				if (errors.chapterViewId) form.showChapterFieldError(errors.chapterViewId);
				if (errors.questionNo) form.showQuestionNoFieldError(errors.questionNo);
				if (errors.correctOption) form.showCorrectFieldError(errors.correctOption);
				if (errors.answerCountMax) form.showAnswerMaxFieldError(errors.answerCountMax);
				if (errors.optionCount) form.showOptionCountFieldError(errors.optionCount);
				form.focusFirstError(errors);

				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showBookFieldValid();
			form.showChapterFieldValid();
			form.showQuestionNoFieldValid();
			form.showCorrectFieldValid();
			form.showAnswerMaxFieldValid();
			form.showOptionCountFieldValid();

			span.end();
			return true;
		});

	/* 最終 submit */
	form.$form
		.off(CONST.EVENT.SUBMIT)
		.on(CONST.EVENT.SUBMIT, (e) => {
			const span = LOG.logStart("KurohonQuestionsInsertUpdateController#submit", {
				level: "INFO",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg =
				errors.sankouBooksViewId ||
				errors.chapterViewId ||
				errors.questionNo ||
				errors.correctOption ||
				errors.answerCountMax ||
				errors.optionCount ||
				"";

			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.sankouBooksViewId) form.showBookFieldError(errors.sankouBooksViewId);
				if (errors.chapterViewId) form.showChapterFieldError(errors.chapterViewId);
				if (errors.questionNo) form.showQuestionNoFieldError(errors.questionNo);
				if (errors.correctOption) form.showCorrectFieldError(errors.correctOption);
				if (errors.answerCountMax) form.showAnswerMaxFieldError(errors.answerCountMax);
				if (errors.optionCount) form.showOptionCountFieldError(errors.optionCount);
				form.focusFirstError(errors);

				span.end();
				return false;
			}

			appendCsrfToForm(form.$form[0]);

			span.end();
			return true;
		});

	initSpan.end();
});
