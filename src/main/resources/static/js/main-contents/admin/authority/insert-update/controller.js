// /js/main-contents/admin/authority/insert-update/controller.js
/* 機能：権限 追加/更新 画面エントリポイント */
import { setValidationInputChange, setValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { appendCsrfToForm } from "/js/util/csrf.js";

// ★ admin 画面なので user/common.js ではなく admin/common.js に寄せる
import { setCommonReady } from "/js/main-contents/admin/common.js";

import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";
import { CONST } from "./const.js";
import { AuthorityInsertUpdateForm } from "./form.js";
import { AuthorityInsertUpdateService } from "./service.js";

const LOG = getLoger("admin.authority.insertUpdate.controller");

/* 共通初期化（admin側） */
setCommonReady(() => {
	const initSpan = LOG.logStart("AuthorityInsertUpdateController#init", { level: "INFO", duration: true });

	/* 初期UI（順序） */
	setFormFloatLabel();
	setAlertDanger();

	const form = new AuthorityInsertUpdateForm($(CONST.SELECTOR.FORM));
	const service = new AuthorityInsertUpdateService(CONST.MESSAGES());

	/* ===== validation targets ===== */
	const TARGETS = ["systemName"]; // name属性ベース

	/* 初期：空の .my-invalid も行高を確保（NBSP + 非表示） */
	form.$form.find(CONST.SELECTOR.INVALID).each(function() {
		const $el = $(this);
		const txt = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
		if (txt.length === 0) $el.text("\u00A0").css("visibility", "hidden");
	});

	/* 入力中バリデーション（即時UX） */
	setValidationInputChange(
		(values) => {
			const span = LOG.logStart("onChangeValidate", { level: "TRACE", duration: false });

			// DOM → Service 期待形へ合わせる（systemName → name）
			const v = { name: String(values.systemName ?? "") };
			const errors = service.validate(v) || {};

			setValidation(errors, TARGETS);

			// 画面への反映
			if (errors.systemName) {
				form.showErrorMessage(errors.systemName);
				form.showNameFieldError(errors.systemName);
				// 高さ維持（可視化）
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
				form.showNameFieldValid();
				// 行高維持しつつ非表示（NBSP + hidden）
				form.$form.find(CONST.SELECTOR.INVALID).each(function() {
					const $el = $(this);
					const t = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
					if (!t) $el.text("\u00A0").css("visibility", "hidden");
				});
			}

			span.end();
			return errors;
		},
		{ getTargetsForAlert: () => TARGETS }
	);

	/* モーダル“開く直前”にも妥当性確認（NGなら開かない） */
	$(CONST.IDS.MODAL)
		.off("before:open.myModal.authority")
		.on("before:open.myModal.authority", (e) => {
			const span = LOG.logStart("modal.beforeOpen", { level: "DEBUG", duration: false });

			const v = form.getValues(); // { name: "..." }
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.systemName) {
				e.preventDefault();
				form.showErrorMessage(errors.systemName);
				form.showNameFieldError(errors.systemName);
				form.focusFirstError(errors);
				return endAndReturn(span, false);
			}

			form.hideErrorMessage();
			form.showNameFieldValid();
			return endAndReturn(span, true);
		});

	/* data-modal ボタン直押し（モーダルに届く前の保険） */
	$(document)
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN, (e) => {
			const span = LOG.logStart("modal.openClick", { level: "DEBUG", duration: false });

			const $btn = $(e.currentTarget);
			const target = $btn.attr(CONST.ATTR.DATA_MODAL) || "";
			if (target !== CONST.IDS.MODAL) return endAndReturn(span, undefined);

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.systemName) {
				e.preventDefault();
				form.showErrorMessage(errors.systemName);
				form.showNameFieldError(errors.systemName);
				form.focusFirstError(errors);
				return endAndReturn(span, false);
			}

			form.hideErrorMessage();
			form.showNameFieldValid();
			return endAndReturn(span, true);
		});

	/* 最終 submit（直接 Enter 送信など） */
	form.$form
		.off(CONST.EVENT.SUBMIT)
		.on(CONST.EVENT.SUBMIT, (e) => {
			const span = LOG.logStart("form.submit", { level: "INFO", duration: true });

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.systemName) {
				e.preventDefault();
				form.showErrorMessage(errors.systemName);
				form.showNameFieldError(errors.systemName);
				form.focusFirstError(errors);
				return endAndReturn(span, false);
			}

			// 通過：CSRF 付与してネイティブ submit
			appendCsrfToForm(form.$form[0]);
			return endAndReturn(span, true);
		});

	// init end
	initSpan.end();
});
