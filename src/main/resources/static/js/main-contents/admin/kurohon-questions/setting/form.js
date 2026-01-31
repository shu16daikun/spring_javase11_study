import { CONST } from "./const.js";

export class KqSettingForm {
	constructor($tableArea) {
		this.$tableArea = $tableArea || $(CONST.SELECTOR.TABLE_AREA);

		this.$deleteForm = $(CONST.SELECTOR.DELETE_FORM);
		this.$detailForm = $(CONST.SELECTOR.DETAIL_FORM);
		this.$deleteTargetName = $(CONST.SELECTOR.DELETE_TARGET_NAME);

		// action prefix/suffix は data-* 属性から取得
		this.deletePrefix =
			this.$deleteForm.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/admin/kurohonQuestions/";
		this.deleteSuffix =
			this.$deleteForm.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/delete";

		this.detailPrefix =
			this.$detailForm.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/admin/kurohonQuestions/";
		this.detailSuffix =
			this.$detailForm.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/browser-guard";
	}

	getTableArea() {
		return this.$tableArea;
	}

	/** 削除モーダル用に「対象名」と form action をセット */
	setDeleteTarget(viewId, displayName) {
		if (!viewId) return;

		this.$deleteTargetName.text(displayName || "");
		const action = this.deletePrefix + encodeURIComponent(viewId) + this.deleteSuffix;
		this.$deleteForm.attr("action", action);
	}

	/** 詳細画面へ遷移するための POST（/admin/kurohonQuestions/{viewId}/browser-guard） */
	submitDetail(viewId) {
		if (!viewId) return;

		const action = this.detailPrefix + encodeURIComponent(viewId) + this.detailSuffix;
		this.$detailForm.attr("action", action);
		this.$detailForm.trigger("submit");
	}
}
