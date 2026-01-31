/* 機能：ADMIN KurohonQuestions “定数・キー・選択子・文言”の一元管理 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation-input-change",
		BOOK_SELECT: "select[name='sankouBooksViewId']",
		CHAPTER_SELECT: "select[name='chapterViewId']",
		QUESTION_NO: "input[name='questionNo']",
		CORRECT_OPTION: "input[name='correctOption']",
		ANSWER_COUNT_MAX: "input[name='answerCountMax']",
		OPTION_COUNT: "input[name='optionCount']",
		QUESTION_HTML: "textarea[name='questionHtml']",
		EXPLANATION_HTML: "textarea[name='explanationHtml']",

		ALERT_ERROR: ".my-alert-danger",
		CONTAINER: ".my-validation-container",
		INVALID: ".my-invalid",
		MODAL_OPEN: ".my-modal-open",
	}),
	IDS: Object.freeze({
		MODAL: "#insert-update-modal",
	}),
	ATTR: Object.freeze({
		DATA_TEXT: "data-text",
		ARIA_INVALID: "aria-invalid",
		DATA_MODAL: "data-modal",
		DATA_BOOK: "data-book",
	}),
	CLASS: Object.freeze({
		IS_INVALID: "my-is-invalid",
		IS_VALID: "my-is-valid",
	}),
	EVENT: Object.freeze({
		SUBMIT: "submit.adminKurohonQuestions",
		CLICK: "click.adminKurohonQuestions",
		CHANGE: "change.adminKurohonQuestions",
	}),
	LABEL: Object.freeze({
		BOOK: "参考書",
		CHAPTER: "章",
		QUESTION_NO: "問題番号",
		CORRECT_OPTION: "正答",
		ANSWER_COUNT_MAX: "最大回答数",
		OPTION_COUNT: "選択肢数",
	}),
	VALIDATION_TARGETS: Object.freeze([
		"sankouBooksViewId",
		"chapterViewId",
		"questionNo",
		"correctOption",
		"answerCountMax",
		"optionCount"
	]),
	LIMIT: Object.freeze({
		QUESTION_NO_MIN: 1,
		QUESTION_NO_MAX: 999,
		CORRECT_OPTION_MAX: 20,
		ANSWER_COUNT_MAX_MIN: 1,
		OPTION_COUNT_MIN: 0,
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
			COMMON_MIN: "error.common.min",
			COMMON_MAX: "error.common.max",
		}),
	}),
	MESSAGES: () => (
		globalThis.VALIDATION_MESSAGES ||
		globalThis.validationMessages ||
		globalThis["validationMessages"] ||
		{}
	),
});
