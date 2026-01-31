// /js/main-contents/user/home.controller.js
/* 機能：ホーム画面エントリポイント */
import { setAlertPrimary, setAlertDanger, setAlertWarning } from "/psfm/js/fragment/alert.js";
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

setCommonReady(() => {
	const LOG = getLoger("USER_HOME");
	LOG.info("page init");

	$(document).ready(function() {
		setAlertPrimary();
		setAlertDanger();
		setAlertWarning();
	});
});
