// /js/main-contents/admin/sankou-books/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";
import { getLoger } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.sankouBooks.setting");

/**
 * 目的: 削除モーダルの反映とPOST先の設定（SankouBooks: DB IDは扱わない）
 * DOM契約:
 * - 表: .table-area > .my-table > table > tbody > tr
 * - 列位置: 0=DB ID, 1=viewId, 2=name, 3=color
 * - フォーム: #delete-form（data-action-prefix/suffix 推奨）
 * - 名称出力: #delete-target-name
 * - 削除ボタン: button.my-btn-danger[data-modal="#delete-modal"]
 */
setCommonReady(() => {
	const initSpan = LOG.logStart("SankouBooksSettingController#init", {
		level: "INFO",
		duration: true
	});

	// 50件/ページのページネーション
	const pgSpan = LOG.logStart("SankouBooksSettingController#attachPagination", {
		level: "INFO",
		duration: true
	});
	attachSimplePagination({
		table: ".my-table table",
		container: "#sbc-pagination",
		perPage: 50,
		labels: { prev: "前へ", next: "次へ" },
		eventNS: ".sbcSetting"
	});
	pgSpan.end();

	/* cache */
	const $table = $(".table-area");
	const $form = $("#delete-form");
	const $name = $("#delete-target-name");

	if ($table.length === 0 || $form.length === 0 || $name.length === 0) {
		LOG.warn(
			"required elements not found. table={0}, form={1}, name={2}",
			$table.length,
			$form.length,
			$name.length
		);
		initSpan.end();
		return;
	}

	// HTML側 th:attr で設定。未設定時は既定値を使用
	const prefix = $form.data("actionPrefix") || "/admin/sankouBooks/";
	const suffix = $form.data("actionSuffix") || "/delete";

	/* 削除ボタンクリック（イベント委譲） */
	$table
		.off("click.sbcSetting", 'button.my-btn-danger[data-modal="#delete-modal"]')
		.on("click.sbcSetting", 'button.my-btn-danger[data-modal="#delete-modal"]', function() {
			const span = LOG.logStart("SankouBooksSettingController#clickDelete", {
				level: "DEBUG",
				duration: true
			});

			const $tr = $(this).closest("tr");
			const $tds = $tr.find("td");

			// 列位置: 0=DB ID, 1=viewId, 2=name
			const viewId = String($tds.eq(1).text() || "").trim();
			const bookName = String($tds.eq(2).text() || "").trim();

			if (!viewId) {
				LOG.warn("delete click ignored: viewId is blank");
				span.end();
				return;
			}

			$name.text(bookName);
			$form.attr("action", prefix + encodeURIComponent(viewId) + suffix);

			span.end();
		});

	initSpan.end();
});
