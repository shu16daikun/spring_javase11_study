// /js/main-contents/user/answer/controller.js
/* 機能：解答画面エントリポイント */
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger } from "/psfm/js/common/loger.js";

import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { wireBtnGroups } from "/psfm/js/fragment/btnGroupVertical.js";
import { appendCsrfToForm } from "/js/util/csrf.js";

import { CONST } from "./const.js";
import { AnswerForm } from "./form.js";
import { AnswerService } from "./service.js";

/* 共通初期化 + 画面固有初期化 */
setCommonReady(() => {
	const LOG = getLoger("USER_ANSWER");

	/* 初期UI */
	setAlertDanger();
	/* 縦・横ともに不足 id/for を補完 */
	wireBtnGroups();

	/* 依存生成 */
	const form = new AnswerForm(CONST.SELECTOR.GROUP);

	// サーバ注入優先 → 互換フォールバック
	const validationMessages = CONST.MESSAGES();
	const service = new AnswerService(validationMessages);

	/* 入力変更：排他・上限・行内エラー（1か所に集約） */
	$(document)
		.off(CONST.EVENT.CHANGE_NS, `${CONST.SELECTOR.GROUP} ${CONST.SELECTOR.CHECKBOX}`)
		.on(
			CONST.EVENT.CHANGE_NS,
			`${CONST.SELECTOR.GROUP} ${CONST.SELECTOR.CHECKBOX}`,
			function() {
				const $group = $(this).closest(CONST.SELECTOR.GROUP);
				if ($group.length === 0) return;

				service.handleGroupChange($group, $(this), form);

				/* 変更時に未選択エラーも更新（UX向上） */
				service.validateGroup($group, form);

				/* log（あなたのログは維持：必要ならここで粒度上げてOK） */
				try {
					const qno = form.getQno($group);
					LOG.debug("answer change qno={0}", qno);
				} catch (_e) {
					// noop
				}
			}
		);

	/* モーダル表示前チェック（終了・次章） */
	$(document)
		.off(CONST.EVENT.BEFORE_OPEN_MODAL, `${CONST.SELECTOR.MODAL_FINISH}, ${CONST.SELECTOR.MODAL_NEXT}`)
		.on(CONST.EVENT.BEFORE_OPEN_MODAL, `${CONST.SELECTOR.MODAL_FINISH}, ${CONST.SELECTOR.MODAL_NEXT}`, function(e) {
			const isFinish = $(this).is(CONST.SELECTOR.MODAL_FINISH);

			/* log */
			LOG.debug("answer beforeOpenModal type={0}", isFinish ? "finish" : "next");

			const invalid = service.validateAll(form);
			if (invalid.count > 0) {
				e.preventDefault();
				service.openValidationForGroup(invalid.first, form);
				form.focusGroup(invalid.first);

				/* log */
				LOG.debug("answer validation failed count={0}", String(invalid.count));
			}
		});

	/* 送信ヘルパ（共通） */
	const submitWithValidation = ($formEl, onBeforeSubmit) => {
		const invalid = service.validateAll(form);
		if (invalid.count > 0) {
			onBeforeSubmit?.(false, invalid);
			return false;
		}
		onBeforeSubmit?.(true);
		appendCsrfToForm($formEl);
		return true;
	};

	/* 「次章へ」送信時：checkboxを next-chapter-form に付け替え */
	$(CONST.SELECTOR.FORM_NEXT)
		.off(CONST.EVENT.SUBMIT_NS)
		.on(CONST.EVENT.SUBMIT_NS, function(e) {
			$(`${CONST.SELECTOR.GROUP} ${CONST.SELECTOR.CHECKBOX}`).attr("form", "next-chapter-form");

			const passed = submitWithValidation(this, (ok, invalid) => {
				if (!ok) {
					e.preventDefault();
					service.openValidationForGroup(invalid.first, form);
					form.focusGroup(invalid.first);

					/* log */
					LOG.debug("submit blocked form=next-chapter-form");
				}
			});
			if (!passed) return;

			/* log */
			LOG.debug("submit ok form=next-chapter-form");
		});

	/* 「終了する」送信時：form 属性を外して finish-form で送る */
	$(CONST.SELECTOR.FORM_FINISH)
		.off(CONST.EVENT.SUBMIT_NS)
		.on(CONST.EVENT.SUBMIT_NS, function(e) {
			$(`${CONST.SELECTOR.GROUP} ${CONST.SELECTOR.CHECKBOX}`).removeAttr("form");

			const passed = submitWithValidation(this, (ok, invalid) => {
				if (!ok) {
					e.preventDefault();
					service.openValidationForGroup(invalid.first, form);
					form.focusGroup(invalid.first);

					/* log */
					LOG.debug("submit blocked form=finish-form");
				}
			});
			if (!passed) return;

			/* log */
			LOG.debug("submit ok form=finish-form");
		});
});
