// /js/main-contents/admin/home/controller.js
/* 機能：ホーム画面エントリポイント */
import { setAlertPrimary, setAlertDanger, setAlertWarning } from "/psfm/js/fragment/alert.js";
import { setCommonReady } from "/js/main-contents/admin/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.home.controller");

setCommonReady(() => {
	const span = LOG.logStart("AdminHomeController#init", { level: "INFO", duration: true });

	setAlertPrimary();
	setAlertDanger();
	setAlertWarning();

	span.end();
});
