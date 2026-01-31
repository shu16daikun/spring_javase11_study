// /js/main-contents/admin/attempt-session/setting/controller.js
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";
import { getLoger, endAndThrow } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.attemptSession.setting.controller");

// ★ ready は setCommonReady に寄せる（$(() => ...) は不要）
setCommonReady(() => {
	const span = LOG.logStart("attachSimplePagination", { level: "INFO", duration: true });

	try {
		attachSimplePagination({
			table: ".my-table table",
			container: "#attempt-session-pagination",
			perPage: 50,
			labels: { prev: "前へ", next: "次へ" },
			eventNS: ".attemptSessionSetting",
		});

		span.end();
	} catch (e) {
		LOG.error("attachSimplePagination failed", e);
		return endAndThrow(span, e);
	}
});
