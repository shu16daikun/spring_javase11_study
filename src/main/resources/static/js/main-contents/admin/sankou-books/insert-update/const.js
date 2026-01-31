/* 機能：ADMIN SankouBooks “定数・キー・選択子・文言”の一元管理 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		FORM: ".my-validation-input-change",
		NAME: "input[name='name']",
		ALERT_ERROR: ".my-alert-danger",
		CONTAINER: ".my-validation-container",
		INVALID: ".my-invalid",
		COLOR_SELECT: "select[name='colorViewId']",
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
		SUBMIT: "submit.adminSankouBooks",
		CLICK: "click.adminSankouBooks",
	}),
	LABEL: Object.freeze({
		NAME: "参考書名",
		COLOR: "カラー",
	}),
	VALIDATION_TARGETS: Object.freeze(["name", "colorViewId"]),
	LIMIT: Object.freeze({
		// HTML 側の maxlength と合わせること（サーバ @Size(max=...) と整合推奨）
		NAME_MAX: 100,
	}),
	KEY: Object.freeze({
		ERROR: Object.freeze({
			COMMON_NOT_BLANK: "error.common.notblank",
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
