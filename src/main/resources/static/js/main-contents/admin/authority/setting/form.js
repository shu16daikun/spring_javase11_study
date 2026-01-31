// /js/main-contents/admin/authority/setting/form.js
import { CONST } from "./const.js";
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.authority.setting.form");

export class AuthoritySettingForm {
	constructor($tableArea) {
		const span = LOG.logStart("AuthoritySettingForm#constructor", { level: "TRACE", duration: false });

		this.$tableArea = $tableArea || $(CONST.SELECTOR.TABLE_AREA);

		this.$deleteForm = $(CONST.SELECTOR.DELETE_FORM);
		this.$detailForm = $(CONST.SELECTOR.DETAIL_FORM);
		this.$deleteTargetName = $(CONST.SELECTOR.DELETE_TARGET_NAME);

		// action prefix/suffix は data-* 属性から取得（Thymeleaf で埋め込み済み想定）
		this.deletePrefix = this.$deleteForm.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/admin/authority/";
		this.deleteSuffix = this.$deleteForm.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/delete";

		this.detailPrefix = this.$detailForm.attr(CONST.ATTR.DATA_ACTION_PREFIX) || "/admin/authority/";
		this.detailSuffix = this.$detailForm.attr(CONST.ATTR.DATA_ACTION_SUFFIX) || "/browser-guard";

		span.end();
	}

	getTableArea() {
		const span = LOG.logStart("AuthoritySettingForm#getTableArea", { level: "TRACE", duration: false });
		return endAndReturn(span, this.$tableArea);
	}

	/** 削除モーダル用に「対象名」と form action をセット */
	setDeleteTarget(viewId, name) {
		const span = LOG.logStart("AuthoritySettingForm#setDeleteTarget", { level: "DEBUG", duration: false });

		if (!viewId) return endAndReturn(span, undefined);

		this.$deleteTargetName.text(name || "");
		const action = this.deletePrefix + encodeURIComponent(viewId) + this.deleteSuffix;
		this.$deleteForm.attr("action", action);

		span.end();
	}

	/** 詳細画面へ遷移するための POST（/admin/authority/{viewId}/browser-guard） */
	submitDetail(viewId) {
		const span = LOG.logStart("AuthoritySettingForm#submitDetail", { level: "INFO", duration: true });

		if (!viewId) return endAndReturn(span, undefined);

		const action = this.detailPrefix + encodeURIComponent(viewId) + this.detailSuffix;
		this.$detailForm.attr("action", action);
		this.$detailForm.trigger("submit");

		span.end();
	}
}
