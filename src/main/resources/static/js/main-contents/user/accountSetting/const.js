// /js/main-contents/user/account_setting/const.js
/* 機能：AccountSetting 画面“定数・キー・選択子・文言”の一元管理 */

export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation-input-change",
		USERNAME: "input[name='username']",
		ALERT_ERROR: ".my-alert-danger",
		CONTAINER: ".my-validation-container",
		INVALID: ".my-invalid",
	}),
	ATTR: Object.freeze({
		DATA_TEXT: "data-text",
		ARIA_INVALID: "aria-invalid",
	}),
	CLASS: Object.freeze({
		IS_INVALID: "my-is-invalid",
		IS_VALID: "my-is-valid",
	}),
	EVENT: Object.freeze({
		SUBMIT_NS: "submit.account",
	}),
	LABEL: Object.freeze({
		USERNAME: "ユーザー名",
	}),
	VALIDATION_TARGETS: Object.freeze(["username"]),
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
		FORM_ID_FALLBACK: "account-setting-form",
		FIELD: Object.freeze({
			USERNAME: "username",
		}),
		ERR_KEY: Object.freeze({
			USERNAME_PATTERN: "error.user.username.pattern",
		}),
	}),
	// サーバ注入の ValidationMessages（無ければ空）
	MESSAGES: () => (
		globalThis.VALIDATION_MESSAGES ||
		globalThis.validationMessages ||
		globalThis["validationMessages"] ||
		{}
	),
});
