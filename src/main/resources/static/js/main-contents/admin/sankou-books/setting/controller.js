// /js/main-contents/admin/sankou-books/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";

import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { SbSettingForm } from "./form.js";

const LOG = getLoger("admin.sankouBooks.setting");

/* 共通初期化（admin 共通処理） */
setCommonReady(() => {
	const initSpan = LOG.logStart("SbSettingController#init", { level: "INFO", duration: true });

	const form = new SbSettingForm($(CONST.SELECTOR.TABLE_AREA));
	const $table = form.getTableArea();

	if ($table.length === 0) {
		LOG.warn("table area not found: {0}", CONST.SELECTOR.TABLE_AREA);
		initSpan.end();
		return;
	}

	/* ========== 1) 削除ボタン（モーダル起動前のセット） ========== */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON, (e) => {
			const span = LOG.logStart("SbSettingController#clickDelete", { level: "DEBUG", duration: true });

			const $btn = $(e.currentTarget);

			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();
			const name = String($btn.attr(CONST.ATTR.DATA_NAME) || "").trim();

			if (!viewId) {
				LOG.warn("delete click but viewId is blank");
				span.end();
				return;
			}

			form.setDeleteTarget(viewId, name || "(不明)");
			span.end();
		});

	/* ========== 2) 詳細ボタン（/browser-guard 経由） ========== */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DETAIL_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DETAIL_BUTTON, (e) => {
			e.preventDefault();

			const span = LOG.logStart("SbSettingController#clickDetail", { level: "DEBUG", duration: true });

			const $btn = $(e.currentTarget);
			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();

			if (!viewId) {
				LOG.warn("detail click but viewId is blank");
				span.end();
				return;
			}

			form.submitDetail(viewId);
			span.end();
		});

	/* ========== 3) ページネーション ========== */
	if (typeof attachSimplePagination === "function") {
		attachSimplePagination({
			table: CONST.PAGINATION.TABLE,
			container: CONST.PAGINATION.CONTAINER,
			perPage: CONST.PAGINATION.PER_PAGE,
			labels: { prev: "前へ", next: "次へ" },
			eventNS: CONST.PAGINATION.EVENT_NS
		});
	} else {
		LOG.warn("attachSimplePagination is not a function");
	}

	initSpan.end();
});
