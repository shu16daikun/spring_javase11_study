// /js/main-contents/user/answer/service.js
/* 機能：解答バリデーション・制御（PropKey依存なし。csrf 等は controller 側） */
import { isBlank } from "/psfm/js/common/utils.js";
import { CONST } from "./const.js";

/* formatMsg フォールバック */
const _formatFallback = (messages = {}, key, label = "", ...args) => {
	let t = messages?.[key] || "";
	if (!t) return label ? `${label}の入力に誤りがあります。` : "入力に誤りがあります。";
	args.forEach((a, i) => { t = t.replace(`{${i + 1}}`, String(a)); });
	return t.replace("{0}", label);
};
const format = globalThis?.formatMsg ? globalThis.formatMsg : _formatFallback;

export class AnswerService {
	constructor(messages = CONST.MESSAGES()) {
		this.messages = messages;
		this.K = CONST.KEY.ERROR;
	}

	/* 1グループの変更処理 */
	handleGroupChange($group, $changed, form) {
		const unknownVal = form.getUnknownValue();
		const isUnknown = String($changed.val()) === unknownVal;

		if (isUnknown) {
			form.enforceUnknownExclusive($group);
			form.clearGroupError($group);
			return;
		}

		form.uncheckUnknownIfNeeded($group);

		const max = form.getMax($group);
		const checkedCount = form.getNormalCheckedCount($group);

		if (checkedCount > max) {
			$changed.prop("checked", false);
			this.showMaxError($group, form, max);
			return;
		}
		form.clearGroupError($group);
	}

	/* グループ単体検証：行内エラー更新＆不正なら true 返す */
	validateGroup($group, form) {
		const max = form.getMax($group);
		const checkedCount = form.getNormalCheckedCount($group);
		const isUnknownChecked = form.isUnknownChecked($group);

		/* 上限超え（通常選択のみカウント） */
		if (checkedCount > max) {
			this.showMaxError($group, form, max);
			return true;
		}

		/* 未選択（通常=0 かつ UNKNOWN未チェック） */
		if (checkedCount === 0 && !isUnknownChecked) {
			const msg = format(this.messages, this.K.COMMON_NOT_BLANK, CONST.LABEL.GROUP);
			form.showGroupError($group, msg);
			return true;
		}

		form.clearGroupError($group);
		return false;
	}

	/* 全体検証 */
	validateAll(form) {
		let firstInvalid = null;
		let invalidCount = 0;

		form.getGroups().each((_, el) => {
			const $group = $(el);
			const isInvalid = this.validateGroup($group, form);
			if (isInvalid) {
				if (!firstInvalid) firstInvalid = $group;
				invalidCount++;
			}
		});

		return { first: firstInvalid, count: invalidCount };
	}

	/* 上限エラー表示（行内） */
	showMaxError($group, form, max) {
		// error.answer.selectedoption.max は {0} を使う → label に max を渡す
		const msg = format(this.messages, this.K.ANS_OPT_MAX, String(max));
		form.showGroupError($group, msg);
	}

	/* モーダル（my-modal系に合わせる） */
	ensureValidationModal() {
		let $modal = $(CONST.SELECTOR.MODAL_VALIDATION);
		if ($modal.length) return $modal;

		const html = `
<div class="my-modal my-modal-dark" id="${CONST.SELECTOR.MODAL_VALIDATION.replace('#', '')}">
	<div class="my-modal-contents">
		<div class="my-modal-dialog">
			<div class="my-modal-content">
				<div class="my-modal-header">
					<h5 class="my-modal-title">${CONST.TEXT.VALIDATION_TITLE}</h5>
					<button type="button" class="my-btn-dark" data-dismiss>&times;</button>
				</div>
				<div class="my-modal-body">
					<p id="${CONST.SELECTOR.MODAL_MESSAGE.replace('#', '')}">${CONST.TEXT.VALIDATION_DEFAULT}</p>
				</div>
				<div class="my-modal-footer">
					<button type="button" class="my-btn-primary" data-dismiss>${CONST.TEXT.OK}</button>
				</div>
			</div>
		</div>
	</div>
</div>`;
		$("body").append(html);
		return $(CONST.SELECTOR.MODAL_VALIDATION);
	}

	openValidationForGroup($group, form, msgOverride) {
		const $modal = this.ensureValidationModal();
		const max = form.getMax($group);

		const msg = !isBlank(msgOverride)
			? String(msgOverride)
			: format(this.messages, this.K.ANS_OPT_MAX, String(max));

		$(CONST.SELECTOR.MODAL_MESSAGE).text(msg);
		$modal.trigger("open.myModal");
	}
}
