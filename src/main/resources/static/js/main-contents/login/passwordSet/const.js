// /js/main-contents/login/password_set/const.js
/* 機能：PasswordSet 画面“定数・キー・選択子・文言”の一元管理 */

export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation-input-change",
		PASSWORD: "input[name='password']",
		PASSWORD_CHECK: "input[name='passwordCheck']",
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
		SUBMIT_NS: "submit.passset",
	}),
	LABEL: Object.freeze({
		PASSWORD: "パスワード",
		PASSWORD_CHECK: "パスワード確認用",
	}),
	PATH: Object.freeze({
		ACTION: "/passwordSet",
	}),
	VALIDATION_TARGETS: Object.freeze(["password", "passwordCheck"]),
	LIMIT: Object.freeze({
		PASSWORD_MIN: 8,
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
			COMMON_MIN: "error.common.min",
			USER_PASSWORD_PATTERN: "error.user.password.pattern",
			USER_PASSWORD_MISMATCH: "error.user.password.mismatch",
		}),
		REGEX: Object.freeze({
			PASSWORD: "regex.password",
		}),
	}),
	LOG: Object.freeze({
		FORM_ID_FALLBACK: "password-set-form",
		FIELD: Object.freeze({
			PASSWORD: "password",
			PASSWORD_CHECK: "passwordCheck",
			GLOBAL: "passwordPolicy",
		}),
		ERR_KEY: Object.freeze({
			PASSWORD_ANY: "error.user.password.*",
			PASSWORD_MISMATCH: "error.user.password.mismatch",
			PASSWORD_PATTERN: "error.user.password.pattern",
		}),
	}),
	// サーバ注入の ValidationMessages（無ければ空）
	MESSAGES: () => (
		globalThis.VALIDATION_MESSAGES ||
		globalThis.validationMessages ||
		(globalThis.PropKey ? globalThis[globalThis.PropKey.VALIDATION_MESSAGES] : null) ||
		{}
	),
});
