// /js/main-contents/admin/sankou-books/insert-update/controller.js
/* 機能：参考書 追加/更新 画面エントリポイント */

import { setValidationInputChange, setValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";

import { appendCsrfToForm } from "/js/util/csrf.js";
import { setCommonReady } from "/js/main-contents/admin/common.js";

import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { SankouBooksInsertUpdateForm } from "./form.js";
import { SankouBooksInsertUpdateService } from "./service.js";

const LOG = getLoger("admin.sankouBooks.insertUpdate");

/* 共通初期化（admin 用） */
setCommonReady(() => {
	const initSpan = LOG.logStart("SankouBooksInsertUpdateController#init", {
		level: "INFO",
		duration: true
	});

	/* 初期UI */
	setFormFloatLabel();
	setAlertDanger();

	const form = new SankouBooksInsertUpdateForm($(CONST.SELECTOR.FORM));
	const service = new SankouBooksInsertUpdateService(CONST.MESSAGES());
	const TARGETS = CONST.VALIDATION_TARGETS;

	/* 空の .my-invalid に行高確保（NBSP + 非表示） */
	form.$form.find(CONST.SELECTOR.INVALID).each(function() {
		const $el = $(this);
		const txt = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
		if (txt.length === 0) $el.text("\u00A0").css("visibility", "hidden");
	});

	/* カラー select 変更 → 再検証 */
	$(document)
		.off("change.adminSankouBooks", CONST.SELECTOR.COLOR_SELECT)
		.on("change.adminSankouBooks", CONST.SELECTOR.COLOR_SELECT, () => {
			const span = LOG.logStart("SankouBooksInsertUpdateController#changeColor", {
				level: "DEBUG",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.colorViewId) {
				form.showErrorMessage(errors.colorViewId);
				form.showColorFieldError(errors.colorViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				// カラーだけでは全体エラー消さない（name側のエラーが残る可能性があるため）
				form.showColorFieldValid();

				// 空ならNBSP隠し（レイアウト維持）
				form.$form.find(CONST.SELECTOR.INVALID).each(function() {
					const $el = $(this);
					const t = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
					if (!t) $el.text("\u00A0").css("visibility", "hidden");
				});
			}

			span.end();
		});

	/* 入力中バリデーション */
	setValidationInputChange(
		(values) => {
			const span = LOG.logStart("SankouBooksInsertUpdateController#inputChangeValidate", {
				level: "TRACE",
				duration: false
			});

			const v = {
				name: String(values.name ?? ""),
				colorViewId: String(values["colorViewId"] ?? "")
			};

			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			// 参考書名
			if (errors.name) {
				form.showNameFieldError(errors.name);
			} else {
				form.showNameFieldValid();
			}

			// カラー
			if (errors.colorViewId) {
				form.showColorFieldError(errors.colorViewId);
			} else {
				form.showColorFieldValid();
			}

			// 先頭エラーをヘッダに出す（両方OKなら消す）
			const firstMsg = errors.name || errors.colorViewId || "";
			if (firstMsg) {
				form.showErrorMessage(firstMsg);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
			}

			// 空ならNBSP隠し
			form.$form.find(CONST.SELECTOR.INVALID).each(function() {
				const $el = $(this);
				const t = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
				if (!t) $el.text("\u00A0").css({ visibility: "hidden" });
			});

			span.end();
			return errors;
		},
		{ getTargetsForAlert: () => TARGETS }
	);

	/* モーダル“開く直前”チェック */
	$(CONST.IDS.MODAL)
		.off("before:open.myModal.sankouBooks")
		.on("before:open.myModal.sankouBooks", (e) => {
			const span = LOG.logStart("SankouBooksInsertUpdateController#beforeOpenModal", {
				level: "DEBUG",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg = errors.name || errors.colorViewId || "";
			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.name) form.showNameFieldError(errors.name);
				if (errors.colorViewId) form.showColorFieldError(errors.colorViewId);
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showNameFieldValid();
			form.showColorFieldValid();

			span.end();
			return true;
		});

	/* data-modal ボタン直押しの保険 */
	$(document)
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN, (e) => {
			const $btn = $(e.currentTarget);
			const target = $btn.attr(CONST.ATTR.DATA_MODAL) || "";
			if (target !== CONST.IDS.MODAL) return;

			const span = LOG.logStart("SankouBooksInsertUpdateController#clickModalOpen", {
				level: "DEBUG",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg = errors.name || errors.colorViewId || "";
			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.name) form.showNameFieldError(errors.name);
				if (errors.colorViewId) form.showColorFieldError(errors.colorViewId);
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showNameFieldValid();
			form.showColorFieldValid();

			span.end();
			return true;
		});

	/* 最終 submit（直接 Enter 送信など） */
	form.$form
		.off(CONST.EVENT.SUBMIT)
		.on(CONST.EVENT.SUBMIT, (e) => {
			const span = LOG.logStart("SankouBooksInsertUpdateController#submit", {
				level: "INFO",
				duration: true
			});

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg = errors.name || errors.colorViewId || "";
			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.name) form.showNameFieldError(errors.name);
				if (errors.colorViewId) form.showColorFieldError(errors.colorViewId);
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			appendCsrfToForm(form.$form[0]);
			span.end();
			return true;
		});

	initSpan.end();
});
