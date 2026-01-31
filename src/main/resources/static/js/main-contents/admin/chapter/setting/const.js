/* 機能：章 設定ホーム 共通定数 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		TABLE_AREA: ".table-area",
		DELETE_FORM: "#delete-form",
		DETAIL_FORM: "#chapter-detail-form",
		DELETE_BUTTON: ".js-chapter-delete",
		DETAIL_BUTTON: ".js-chapter-detail",
		DELETE_TARGET_NAME: "#delete-target-name",
	}),
	ATTR: Object.freeze({
		DATA_ACTION_PREFIX: "data-action-prefix",
		DATA_ACTION_SUFFIX: "data-action-suffix",
		DATA_VIEW_ID: "data-view-id",
		DATA_NAME: "data-name",
	}),
	EVENT: Object.freeze({
		CLICK: "click.chapter.setting",
	}),
	PAGINATION: Object.freeze({
		TABLE: ".my-table table",
		CONTAINER: "#chapter-pagination",
		PER_PAGE: 50,
		EVENT_NS: ".chapterSetting",
	}),
});
