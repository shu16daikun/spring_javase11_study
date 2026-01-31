// /js/main-contents/admin/users/insert-update/const.js
/* 機能：ADMIN Users “定数・キー・選択子・文言”の一元管理 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation-input-change",
		USERNAME: "input[name='username']",
		ALERT_ERROR: ".my-alert-danger",
		CONTAINER: ".my-validation-container",
		INVALID: ".my-invalid",
		AUTH_SELECT: "select[name='authorityViewId']",
		MODAL_OPEN: ".my-modal-open",
	}),
	IDS: Object.freeze({
		MODAL: "#insert-update-modal",
		PASS_RESET_MODAL: "#pass-reset-modal",
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
		SUBMIT: "submit.adminUsers",
		CLICK: "click.adminUsers",
	}),
	LABEL: Object.freeze({
		USERNAME: "ユーザー名",
		AUTHORITY: "権限",
	}),
	VALIDATION_TARGETS: Object.freeze(["username", "authorityViewId"]),
	LIMIT: Object.freeze({
		USERNAME_MIN: 8,
		USERNAME_MAX: 20,
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
			COMMON_SIZE: "error.common.size",
			USER_USERNAME_PATTERN: "error.user.username.pattern",
		}),
		REGEX: Object.freeze({
			USERNAME: "regex.username",
		}),
	}),
	LOG: Object.freeze({
		ERR_KEY: Object.freeze({
			USERNAME_PATTERN: "error.user.username.pattern",
		}),
	}),
	MESSAGES: () => (
		globalThis.VALIDATION_MESSAGES ||
		globalThis.validationMessages ||
		globalThis["validationMessages"] ||
		{}
	),
});
