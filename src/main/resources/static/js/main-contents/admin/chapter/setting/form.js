// /js/main-contents/admin/chapter/setting/form.js
import { getLoger } from "/psfm/js/common/loger.js";
import { CONST } from "./const.js";

const LOG = getLoger("admin.chapter.setting.form");

export class ChapterSettingForm {
	constructor($tableArea) {
		const span = LOG.logStart("ChapterSettingForm#constructor", { level: "TRACE", duration: false });

		this.$tableArea = $tableArea || $(CONST.SELECTOR.TABLE_AREA);

		this.$deleteForm = $(CONST.SELECTOR.DELETE_FORM);
		this.$detailForm = $(CONST.SELECTOR.DETAIL_FORM);
		this.$deleteTargetName = $(CONST.SELECTOR.DELETE_TARGET_NAME);

		// action prefix/suffix は data-* 属性から取得（Thymeleaf で埋め込み）
		this.deletePrefix = this.$deleteForm.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/admin/chapter/";
		this.deleteSuffix = this.$deleteForm.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/delete";

		this.detailPrefix = this.$detailForm.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/admin/chapter/";
		this.detailSuffix = this.$detailForm.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/browser-guard";

		span.end();
	}

	getTableArea() {
		return this.$tableArea;
	}

	/** 削除モーダル用に「対象名」と form action をセット */
	setDeleteTarget(viewId, displayName) {
		const span = LOG.logStart("ChapterSettingForm#setDeleteTarget", { level: "DEBUG", duration: false });

		if (!viewId) {
			span.end();
			return;
		}

		this.$deleteTargetName.text(displayName || "");
		const action = this.deletePrefix + encodeURIComponent(viewId) + this.deleteSuffix;
		this.$deleteForm.attr("action", action);

		span.end();
	}

	/** 詳細画面へ遷移するための POST（/admin/chapter/{viewId}/browser-guard） */
	submitDetail(viewId) {
		const span = LOG.logStart("ChapterSettingForm#submitDetail", { level: "INFO", duration: true });

		if (!viewId) {
			span.end();
			return;
		}

		const action = this.detailPrefix + encodeURIComponent(viewId) + this.detailSuffix;
		this.$detailForm.attr("action", action);
		this.$detailForm.trigger("submit");

		span.end();
	}
}
