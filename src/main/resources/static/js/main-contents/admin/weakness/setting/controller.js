import { setCommonReady } from "/js/main-contents/admin/common.js";
import { attachSimplePagination } from "/psfm/js/fragment/pagination.js";

import { getLoger } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.weakness.setting");

setCommonReady(() => {
	const span = LOG.logStart("WeaknessSettingController#init", { level: "INFO", duration: true });

	attachSimplePagination({
		table: ".my-table table",
		container: "#weakness-pagination",
		perPage: 50,
		labels: { prev: "前へ", next: "次へ" },
		eventNS: ".weaknessSetting",
	});

	span.end();
});
