// /js/main-contents/user/sankouBooks.controller.js
/* 機能：参考書一覧画面エントリポイント */
import { setCommonReady } from "/js/main-contents/user/common.js";
import { setAccordion } from "/psfm/js/fragment/accordion.js";
import { getLoger } from "/psfm/js/common/loger.js";

setCommonReady(() => {
	const LOG = getLoger("USER_SANKOU_BOOKS");
	LOG.info("page init");

	$(document).ready(function() {
		setAccordion();
	});
});
