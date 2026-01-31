// /js/main-contents/user/chapter/controller.js
/* 機能：チャプター画面エントリポイント */
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { ChapterForm } from "./form.js";

/* 共通初期化 */
setCommonReady(() => {
	const LOG = getLoger("USER_CHAPTER");

	$(() => {
		LOG.info("page init");

		const form = new ChapterForm($(CONST.SELECTOR.FORM));
		const $list = $(CONST.SELECTOR.LIST_GROUP);

		if ($list.length === 0) {
			return;
		}

		// list-group 内の aクリックを拾って、共通フォームで POST する
		$list
			.off(CONST.EVENT.CLICK, CONST.SELECTOR.LINK)
			.on(CONST.EVENT.CLICK, CONST.SELECTOR.LINK, (e) => {
				e.preventDefault();

				const $link = $(e.currentTarget);
				const bookViewId = String($link.attr(CONST.ATTR.DATA_BOOK_VIEW_ID) || "").trim();
				const chapterNo = String($link.attr(CONST.ATTR.DATA_CHAPTER_NO) || "").trim();

				if (!bookViewId || !chapterNo) {
					return;
				}

				// クリック→遷移ログ（必要最低限）
				LOG.debug("navigate bookViewId={0} chapterNo={1}", bookViewId, chapterNo);

				// ここは同期POST想定：duration計測したければ sessionStorage 方式にする
				form.submit(bookViewId, chapterNo);
			});
	});
});
