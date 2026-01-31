/* 機能：ADMIN Chapter “定数・キー・選択子・文言”の一元管理 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation-input-change",
		NO: "input[name='no']",
		TITLE: "input[name='title']",
		BOOK_SELECT: "select[name='sankouBooksViewId']",
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
	}),
	CLASS: Object.freeze({
		IS_INVALID: "my-is-invalid",
		IS_VALID: "my-is-valid",
	}),
	EVENT: Object.freeze({
		SUBMIT: "submit.adminChapter",
		CLICK: "click.adminChapter",
	}),
	LABEL: Object.freeze({
		NO: "章番号",
		TITLE: "章タイトル",
		BOOK: "参考書",
	}),
	VALIDATION_TARGETS: Object.freeze(["no", "title", "sankouBooksViewId"]),
	LIMIT: Object.freeze({
		NO_MIN: 1,
		NO_MAX: 99,
		TITLE_MAX: 100, // サーバ側 ChapterFormParam.TITLE_MAX と揃える
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
