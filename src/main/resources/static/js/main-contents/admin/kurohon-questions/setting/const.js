/* 機能：黒本問題 設定ホーム 共通定数 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		TABLE_AREA: ".table-area",
		DELETE_FORM: "#delete-form",
		DETAIL_FORM: "#kq-detail-form",
		DELETE_BUTTON: ".js-kq-delete",
		DETAIL_BUTTON: ".js-kq-detail",
		DELETE_TARGET_NAME: "#delete-target-name",
	}),
	ATTR: Object.freeze({
		DATA_ACTION_PREFIX: "data-action-prefix",
		DATA_ACTION_SUFFIX: "data-action-suffix",
		DATA_VIEW_ID: "data-view-id",
		DATA_NAME: "data-name",
	}),
	EVENT: Object.freeze({
		CLICK: "click.kq.setting",
	}),
	PAGINATION: Object.freeze({
		TABLE: ".my-table table",
		CONTAINER: "#kq-pagination",
		PER_PAGE: 50,
		EVENT_NS: ".kqSetting",
	}),
});
