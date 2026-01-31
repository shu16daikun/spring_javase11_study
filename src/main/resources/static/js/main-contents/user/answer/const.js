// /js/main-contents/user/answer/const.js
/* 機能：Answer 画面“定数・キー・選択子・文言”の一元管理 */

export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		GROUP: ".my-btn-group-horizontal.has-unknown",
		CHECKBOX: "input[type='checkbox']",
		FORM_NEXT: "#next-chapter-form",
		FORM_FINISH: "#finish-form",
		MODAL_FINISH: "#finish-modal",
		MODAL_NEXT: "#next-modal",
		MODAL_VALIDATION: "#validation-modal",
		MODAL_MESSAGE: "#validation-message",
		INVALID_LABEL: ".my-invalid",
	}),
	ATTR: Object.freeze({
		DATA_MAX: "data-max",
		DATA_QNO: "data-qno",
		DATA_TEXT: "data-text",
	}),
	CLASS: Object.freeze({
		IS_INVALID: "is-invalid",
		IS_VALID: "is-valid",
	}),
	EVENT: Object.freeze({
		CHANGE_NS: "change.answer",
		SUBMIT_NS: "submit.answer",
		BEFORE_OPEN_MODAL: "before:open.myModal",
	}),
	CONST: Object.freeze({
		UNKNOWN_VALUE: "UNKNOWN",
	}),
	LABEL: Object.freeze({
		GROUP: "選択肢", // 未選択エラー用の代表ラベル
	}),
	TEXT: Object.freeze({
		VALIDATION_TITLE: "入力エラー",
		VALIDATION_DEFAULT: "選択に誤りがあります。",
		OK: "OK",
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
			ANS_OPT_MAX: "error.answer.selectedoption.max",
		}),
	}),
	FN: Object.freeze({
		buildInvalidIdByQuestionNo: (qno) => `${String(qno).padStart(2, "0")}_invalid`,
	}),
	// サーバ注入の ValidationMessages（無ければ空）
	MESSAGES: () => (
		globalThis.VALIDATION_MESSAGES ||
		globalThis.validationMessages ||
		(globalThis.PropKey ? globalThis[globalThis.PropKey.VALIDATION_MESSAGES] : null) ||
		{}
	),
});
