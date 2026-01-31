// /js/main-contents/admin/answer-records/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";
import { getLoger, endAndThrow } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.answerRecords.setting.controller");

// ★ ready は setCommonReady に寄せる（$(() => ...) は不要になる）
setCommonReady(() => {
	const span = LOG.logStart("attachSimplePagination", { level: "INFO", duration: true });

	try {
		attachSimplePagination({
			table: ".my-table table", // tbody > tr を対象にする
			container: "#answer-records-pagination", // ナビの描画先
			perPage: 50,
			labels: { prev: "前へ", next: "次へ" },
			eventNS: ".answerRecordsSetting",
		});

		span.end();
	} catch (e) {
		LOG.error("attachSimplePagination failed", e);
		return endAndThrow(span, e);
	}
});
