// /js/main-contents/user/error.controller.js
/* 機能：エラー画面エントリポイント */
import { setAlertDanger, setAlertPrimary } from "/psfm/js/fragment/alert.js";
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

setCommonReady(() => {
	const LOG = getLoger("USER_ERROR");
	LOG.info("page init");

	$(document).ready(function() {
		setAlertDanger();
		setAlertPrimary();
	});
});
