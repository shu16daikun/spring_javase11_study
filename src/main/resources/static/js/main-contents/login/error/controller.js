// /js/main-contents/login/error.controller.js
/* 機能：エラー画面エントリポイント */
import { setAlertDanger, setAlertPrimary } from "/psfm/js/fragment/alert.js";
import { setCommonReady } from "../common.js";

setCommonReady(() => {
	setAlertDanger();
	setAlertPrimary();
});
