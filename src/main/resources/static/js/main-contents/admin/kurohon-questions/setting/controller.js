// /js/main-contents/admin/kurohon-questions/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { KqSettingForm } from "./form.js";

const LOG = getLoger("admin.kurohonQuestions.setting");

/**
 * 目的:
 *  - 黒本問題一覧のページネーション（50件/ページ）
 *  - 削除モーダルの反映とPOST先の設定（DB IDは扱わない）
 *  - 詳細ボタンから中継URL (/browser-guard) への POST
 */
setCommonReady(() => {
	const initSpan = LOG.logStart("KurohonQuestionsSettingController#init", {
		level: "INFO",
		duration: true
	});

	const form = new KqSettingForm($(CONST.SELECTOR.TABLE_AREA));
	const $table = form.getTableArea();

	if ($table.length === 0) {
		LOG.warn("tableArea not found: {0}", CONST.SELECTOR.TABLE_AREA);
		initSpan.end();
		return;
	}

	/* ========== 1) 削除モーダル ========== */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON, (e) => {
			const span = LOG.logStart("KurohonQuestionsSettingController#clickDelete", {
				level: "DEBUG",
				duration: true
			});

			const $btn = $(e.currentTarget);
			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();
			const name = String($btn.attr(CONST.ATTR.DATA_NAME) || "").trim();

			if (!viewId) {
				LOG.warn("delete click ignored: viewId is blank");
				span.end();
				return;
			}

			// name は「参考書名 章 問XX」形式を想定（Thymeleaf 側で組み立て）
			form.setDeleteTarget(viewId, name || "(不明)");
			span.end();
		});

	/* ========== 2) 詳細ボタン（/browser-guard 経由） ========== */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DETAIL_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DETAIL_BUTTON, (e) => {
			const span = LOG.logStart("KurohonQuestionsSettingController#clickDetail", {
				level: "DEBUG",
				duration: true
			});

			e.preventDefault();

			const $btn = $(e.currentTarget);
			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();

			if (!viewId) {
				LOG.warn("detail click ignored: viewId is blank");
				span.end();
				return;
			}

			form.submitDetail(viewId);
			span.end();
		});

	/* ========== 3) ページネーション ========== */
	if (typeof attachSimplePagination === "function") {
		const span = LOG.logStart("KurohonQuestionsSettingController#attachPagination", {
			level: "INFO",
			duration: true
		});

		attachSimplePagination({
			table: CONST.PAGINATION.TABLE,
			container: CONST.PAGINATION.CONTAINER,
			perPage: CONST.PAGINATION.PER_PAGE,
			labels: { prev: "前へ", next: "次へ" },
			eventNS: CONST.PAGINATION.EVENT_NS
		});

		span.end();
	} else {
		LOG.warn("attachSimplePagination is not a function");
	}

	initSpan.end();
});
