// /js/main-contents/login/const.js
/* 機能：Login 画面の“定数・キー・選択子・文言”を一元管理 */

export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation",
		USERNAME: "[name='username']",
		PASSWORD: "[name='password']",
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
		SUBMIT_NS: "submit.login",
	}),
	LABEL: Object.freeze({
		USERNAME: "ユーザー名",
		PASSWORD: "パスワード",
	}),
	PATH: Object.freeze({
		ACTION: "/login",
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
			// ここに login 固有キーを増やしたくなったら追記
		}),
	}),
	LOG: Object.freeze({
		FORM_ID_FALLBACK: "login-form",
		FIELD: Object.freeze({
			USERNAME: "username",
			PASSWORD: "password",
			GLOBAL: "loginGlobal",
		}),
		ERR_KEY: Object.freeze({
			USERNAME: "error.login.username",
			PASSWORD: "error.login.password",
			MISMATCH: "error.login.mismatch",
		}),
	}),
	// サーバ注入の ValidationMessages（なければ空）
	MESSAGES: () => (
		globalThis.VALIDATION_MESSAGES ||
		globalThis.validationMessages ||
		(globalThis.PropKey ? globalThis[globalThis.PropKey.VALIDATION_MESSAGES] : null) ||
		{}
	),
});
