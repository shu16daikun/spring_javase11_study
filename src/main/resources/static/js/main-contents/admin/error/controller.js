// /js/main-contents/admin/error/controller.js
/* 機能：エラー画面エントリポイント */
import { setAlertDanger, setAlertPrimary } from "/psfm/js/fragment/alert.js";
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.error.controller");

setCommonReady(() => {
	const span = LOG.logStart("AdminErrorController#init", { level: "INFO", duration: true });

	setAlertDanger();
	setAlertPrimary();

	span.end();
});
