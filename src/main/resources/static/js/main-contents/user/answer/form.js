// /js/main-contents/user/answer/form.js
/* 機能：解答フォーム操作 */
import { CONST } from "./const.js";

export class AnswerForm {
	constructor(selectorGroup) {
		this.selectorGroup = selectorGroup || CONST.SELECTOR.GROUP;
	}

	/* 全グループ取得 */
	getGroups() { return $(this.selectorGroup); }

	/* 最大選択数（未設定は 1） */
	getMax($group) {
		const v = Number($group.attr(CONST.ATTR.DATA_MAX));
		return Number.isFinite(v) ? v : 1;
	}

	/* 設問番号（data-qno → タイトル文字 fallback） */
	getQno($group) {
		const q = $group.attr(CONST.ATTR.DATA_QNO);
		if (q) return String(q); // ← ゼロ埋めしない
		const t = $group.find(".my-btn-option-title").first().text() || "";
		return t.replace(/\D/g, ""); // ← ゼロ埋めしない
	}

	/* UNKNOWN値 */
	getUnknownValue() { return CONST.CONST.UNKNOWN_VALUE; }

	/* グループ内チェックボックス */
	getGroupCheckboxes($group) { return $group.find(CONST.SELECTOR.CHECKBOX); }

	/* UNKNOWNチェックボックス */
	getUnknownCheckbox($group) {
		const val = this.getUnknownValue();
		return this.getGroupCheckboxes($group).filter((_, el) => $(el).val() === val);
	}

	isUnknownChecked($group) { return this.getUnknownCheckbox($group).is(":checked"); }

	/* 通常選択（UNKNOWN除外） */
	getNormalCheckboxes($group) {
		const val = this.getUnknownValue();
		return this.getGroupCheckboxes($group).filter((_, el) => $(el).val() !== val);
	}

	/* 選択数（UNKNOWN除外） */
	getNormalCheckedCount($group) { return this.getNormalCheckboxes($group).filter(":checked").length; }

	/* 行内エラー表示／消去（id=XX_invalid 想定） */
	showGroupError($group, message) {
		const qno = this.getQno($group);
		const $invalidLabel = $(`#${CONST.FN.buildInvalidIdByQuestionNo(qno)}`);

		$group.addClass(CONST.CLASS.IS_INVALID).removeClass(CONST.CLASS.IS_VALID);
		// レイアウト確保しつつ可視にする
		$invalidLabel
			.attr(CONST.ATTR.DATA_TEXT, message)
			.text(String(message))
			.css("visibility", "visible");
	}

	clearGroupError($group) {
		const qno = this.getQno($group);
		const $invalidLabel = $(`#${CONST.FN.buildInvalidIdByQuestionNo(qno)}`);

		$group.removeClass(CONST.CLASS.IS_INVALID).addClass(CONST.CLASS.IS_VALID);
		// 空文言でも1行分の高さを維持する：NBSP + visibility:hidden
		$invalidLabel
			.attr(CONST.ATTR.DATA_TEXT, "")
			.text("\u00A0")
			.css("visibility", "hidden");
	}

	/* UNKNOWN 単独を強制 */
	enforceUnknownExclusive($group) {
		const $unknown = this.getUnknownCheckbox($group);
		const $normals = this.getNormalCheckboxes($group);
		if ($unknown.prop("checked")) $normals.prop("checked", false);
	}

	/* 通常選択がある場合 UNKNOWN 解除 */
	uncheckUnknownIfNeeded($group) {
		const $unknown = this.getUnknownCheckbox($group);
		const hasNormal = this.getNormalCheckedCount($group) > 0;
		if (hasNormal) $unknown.prop("checked", false);
	}

	/* フォーカス */
	focusGroup($group) {
		const $first = this.getGroupCheckboxes($group).first();
		if ($first.length) $first.trigger("focus");
	}
}
