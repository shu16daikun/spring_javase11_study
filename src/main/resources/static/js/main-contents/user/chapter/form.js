// /js/main-contents/user/chapter/form.js
import { CONST } from "./const.js";

export class ChapterForm {
	constructor($formRoot) {
		this.$form = $formRoot || $(CONST.SELECTOR.FORM);

		// action prefix/suffix は data-* 属性から取得し、無ければデフォルト
		this.prefix =
			this.$form.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/user/sankouBooks/";
		this.suffix =
			this.$form.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/browser-guard";
	}

	/**
	 * /user/sankouBooks/{bookViewId}/{chapterNo}/browser-guard へ POST する
	 */
	submit(bookViewId, chapterNo) {
		const b = String(bookViewId || "").trim();
		const c = String(chapterNo || "").trim();
		if (!b || !c) return;

		const action =
			this.prefix +
			encodeURIComponent(b) +
			"/" +
			encodeURIComponent(c) +
			this.suffix;

		this.$form.attr("action", action);
		this.$form.trigger("submit");
	}
}
