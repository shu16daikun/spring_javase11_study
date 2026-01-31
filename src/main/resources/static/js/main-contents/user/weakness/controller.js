// /js/main-contents/user/weakness/controller.js
/* 機能：weakness画面エントリポイント */
import { setCommonReady } from "/js/main-contents/user/common.js";
import { setAccordion } from "/psfm/js/fragment/accordion.js";
import { getLoger } from "/psfm/js/common/loger.js";

setCommonReady(() => {
	const LOG = getLoger("USER_WEAKNESS");

	$(document).ready(function() {
		LOG.info("page init");
		setAccordion();
	});
});
