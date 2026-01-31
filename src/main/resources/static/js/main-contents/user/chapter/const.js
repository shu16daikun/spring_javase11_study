// /js/main-contents/user/chapter/const.js
/* 機能：ユーザー章選択 共通定数 */
export const CONST = Object.freeze({
	SELECTOR: Object.freeze({
		LIST_GROUP: ".my-list-group",
		LINK: ".js-chapter-link",
		FORM: "#chapter-form",
	}),
	ATTR: Object.freeze({
		DATA_ACTION_PREFIX: "data-action-prefix",
		DATA_ACTION_SUFFIX: "data-action-suffix",
		DATA_BOOK_VIEW_ID: "data-book-view-id",
		DATA_CHAPTER_NO: "data-chapter-no",
	}),
	EVENT: Object.freeze({
		CLICK: "click.user.chapter",
	}),
});
