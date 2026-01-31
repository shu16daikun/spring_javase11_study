// /js/main-contents/admin/chapter/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { ChapterSettingForm } from "./form.js";

const LOG = getLoger("admin.chapter.setting.controller");

/* 共通初期化（admin 用） */
setCommonReady(() => {
	const initSpan = LOG.logStart("ChapterSettingController#init", { level: "INFO", duration: true });

	const form = new ChapterSettingForm($(CONST.SELECTOR.TABLE_AREA));
	const $table = form.getTableArea();

	if ($table.length === 0) {
		LOG.warn("tableArea not found: {0}", CONST.SELECTOR.TABLE_AREA);
		initSpan.end();
		return;
	}

	/* === 削除ボタン（モーダル表示前に対象をセット） === */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON, (e) => {
			const span = LOG.logStart("click.deleteButton", { level: "INFO", duration: true });

			const $btn = $(e.currentTarget);
			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();
			const rawName = String($btn.attr(CONST.ATTR.DATA_NAME) || "").trim();

			if (!viewId) {
				LOG.warn("deleteButton: viewId is blank");
				span.end();
				return;
			}

			const display = rawName || "";
			form.setDeleteTarget(viewId, display);

			span.end();
		});

	/* === 詳細ボタン（中間URL /browser-guard へ POST） === */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DETAIL_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DETAIL_BUTTON, (e) => {
			const span = LOG.logStart("click.detailButton", { level: "INFO", duration: true });

			e.preventDefault();

			const $btn = $(e.currentTarget);
			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();
			if (!viewId) {
				LOG.warn("detailButton: viewId is blank");
				span.end();
				return;
			}

			form.submitDetail(viewId);

			span.end();
		});

	/* === ページネーション === */
	if (typeof attachSimplePagination === "function") {
		const span = LOG.logStart("attachSimplePagination", { level: "DEBUG", duration: true });

		attachSimplePagination({
			table: CONST.PAGINATION.TABLE,
			container: CONST.PAGINATION.CONTAINER,
			perPage: CONST.PAGINATION.PER_PAGE,
			labels: { prev: "前へ", next: "次へ" },
			eventNS: CONST.PAGINATION.EVENT_NS,
		});

		span.end();
	}

	initSpan.end();
});
