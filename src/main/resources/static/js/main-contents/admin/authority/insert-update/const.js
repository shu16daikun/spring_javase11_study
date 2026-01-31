// /js/main-contents/admin/authority/insert-update/const.js
/* 機能：Authority Insert/Update 共通の定数集約（文字リテラル排除） */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: "#insert-update-form",
		NAME: "input[name='systemName']",
		ALERT_ERROR: ".my-alert-danger",
		CONTAINER: ".my-validation-container",
		INVALID: ".my-invalid",
		MODAL_OPEN: ".my-modal-open",
	}),
	IDS: Object.freeze({
		MODAL: "#insert-update-modal",
	}),
	ATTR: Object.freeze({
		DATA_MODAL: "data-modal",
		DATA_SUBMIT_FORM: "data-submit-form",
		DATA_BIND_FROM: "data-bind-from",
		DATA_BIND_TO: "data-bind-to",
		DATA_BIND_TEMPLATE: "data-bind-template",
		ARIA_INVALID: "aria-invalid",
		DATA_TEXT: "data-text",
	}),
	CLASS: Object.freeze({
		IS_INVALID: "my-is-invalid",
		IS_VALID: "my-is-valid",
	}),
	EVENT: Object.freeze({
		NS: ".authority.insertUpdate",
		INPUT: "input.authority.insertUpdate",
		CHANGE: "change.authority.insertUpdate",
		BLUR: "blur.authority.insertUpdate",
		SUBMIT: "submit.authority.insertUpdate",
		CLICK: "click.authority.insertUpdate",
		BEFORE_OPEN_MODAL: "before:open.myModal.authority.insertUpdate",
	}),
	LABEL: Object.freeze({
		NAME: "権限名",
	}),
	LIMIT: Object.freeze({
		NAME_MIN: 1,
		NAME_MAX: 15,
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
			COMMON_SIZE: "error.common.size",
			AUTH_SYSTEM_NAME_PATTERN: "error.authority.systemname.pattern",
		}),
		REGEX: Object.freeze({
			AUTH_SYSTEM_NAME: "regex.authority.systemname",
		}),
	}),
	// 既定のメッセージ供給源（ValidationMessageUtil が積む想定）
	MESSAGES: () => (globalThis.VALIDATION_MESSAGES || globalThis.validationMessages || {}),
});
