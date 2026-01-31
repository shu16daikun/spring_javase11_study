// /js/main-contents/admin/authority/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { AuthoritySettingForm } from "./form.js";

const LOG = getLoger("admin.authority.setting.controller");

/* 共通初期化（admin 用） */
setCommonReady(() => {
	const initSpan = LOG.logStart("AuthoritySettingController#init", { level: "INFO", duration: true });

	const form = new AuthoritySettingForm($(CONST.SELECTOR.TABLE_AREA));
	const $table = form.getTableArea();

	/* === 削除ボタン（モーダル表示前に対象をセット） === */
	$table
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.DELETE_BUTTON, (e) => {
			const span = LOG.logStart("click.deleteButton", { level: "DEBUG", duration: false });

			const $btn = $(e.currentTarget);

			// ボタンに埋め込んだ data-* から取得
			const viewId = String($btn.attr(CONST.ATTR.DATA_VIEW_ID) || "").trim();
			const name = String($btn.attr(CONST.ATTR.DATA_NAME) || "").trim();

			if (!viewId) {
				// viewId 取れない場合は安全側で何もしない
				span.end();
				return;
			}

			form.setDeleteTarget(viewId, name);
			// 実際のモーダル表示は psfm 側の my-modal が担当（data-modal="#delete-modal"）

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
				span.end();
				return;
			}

			// /admin/authority/{viewId}/browser-guard へ POST
			form.submitDetail(viewId);

			span.end();
		});

	/* === ページネーション === */
	if (typeof attachSimplePagination === "function") {
		const span = LOG.logStart("attachSimplePagination", { level: "INFO", duration: true });

		attachSimplePagination({
			table: CONST.PAGINATION.TABLE, // 一覧テーブル
			container: CONST.PAGINATION.CONTAINER, // ナビ置き場
			perPage: CONST.PAGINATION.PER_PAGE,
			labels: { prev: "前へ", next: "次へ" },
			eventNS: CONST.PAGINATION.EVENT_NS,
		});

		span.end();
	}

	initSpan.end();
});
