// /js/main-contents/admin/chapter/insert-update/controller.js
import { setValidationInputChange, setValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { appendCsrfToForm } from "/js/util/csrf.js";
import { setCommonReady } from "/js/main-contents/user/common.js";
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { ChapterInsertUpdateForm } from "./form.js";
import { ChapterInsertUpdateService } from "./service.js";

const LOG = getLoger("admin.chapter.insert-update.controller");

/* 共通初期化（ready二重防止でコールバックに寄せる） */
setCommonReady(() => {
	const initSpan = LOG.logStart("ChapterInsertUpdateController#init", { level: "INFO", duration: true });

	/* 初期UI */
	setFormFloatLabel();
	setAlertDanger();

	const form = new ChapterInsertUpdateForm($(CONST.SELECTOR.FORM));
	const service = new ChapterInsertUpdateService(CONST.MESSAGES());
	const TARGETS = CONST.VALIDATION_TARGETS;

	/* 空の .my-invalid に行高確保（NBSP入れて非表示） */
	form.$form.find(CONST.SELECTOR.INVALID).each(function() {
		const $el = $(this);
		const txt = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
		if (txt.length === 0) $el.text("\u00A0").css("visibility", "hidden");
	});

	const hideEmptyInvalids = () => {
		form.$form.find(CONST.SELECTOR.INVALID).each(function() {
			const $el = $(this);
			const t = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
			if (!t) $el.text("\u00A0").css({ visibility: "hidden" });
		});
	};

	const validateAndReflect = (src) => {
		const span = LOG.logStart(`validateAndReflect(${src})`, { level: "DEBUG", duration: true });

		const v = form.getValues();
		const errors = service.validate(v) || {};
		setValidation(errors, TARGETS);

		// 章番号
		if (errors.no) {
			form.showErrorMessage(errors.no);
			form.showNoFieldError(errors.no);
			form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
		} else {
			form.showNoFieldValid();
		}

		// タイトル
		if (errors.title) {
			form.showErrorMessage(errors.title);
			form.showTitleFieldError(errors.title);
			form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
		} else {
			form.showTitleFieldValid();
		}

		// 参考書
		if (errors.sankouBooksViewId) {
			form.showErrorMessage(errors.sankouBooksViewId);
			form.showBookFieldError(errors.sankouBooksViewId);
			form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
		} else {
			form.showBookFieldValid();
		}

		hideEmptyInvalids();

		return endAndReturn(span, errors);
	};

	/* 参考書 select 変更 → 再検証 */
	$(document)
		.off("change.adminChapter", CONST.SELECTOR.BOOK_SELECT)
		.on("change.adminChapter", CONST.SELECTOR.BOOK_SELECT, () => {
			const span = LOG.logStart("change.bookSelect", { level: "INFO", duration: true });
			validateAndReflect("bookSelect");
			span.end();
		});

	/* 入力中バリデーション */
	setValidationInputChange(
		(values) => {
			const span = LOG.logStart("setValidationInputChange", { level: "TRACE", duration: false });

			const v = {
				no: String(values.no ?? ""),
				title: String(values.title ?? ""),
				sankouBooksViewId: String(values["sankouBooksViewId"] ?? ""),
			};

			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			// 反映（values由来なので、form.getValues()は使わずここで分岐）
			if (errors.no) {
				form.showErrorMessage(errors.no);
				form.showNoFieldError(errors.no);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.showNoFieldValid();
			}

			if (errors.title) {
				form.showErrorMessage(errors.title);
				form.showTitleFieldError(errors.title);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.showTitleFieldValid();
			}

			if (errors.sankouBooksViewId) {
				form.showErrorMessage(errors.sankouBooksViewId);
				form.showBookFieldError(errors.sankouBooksViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.showBookFieldValid();
			}

			hideEmptyInvalids();

			span.end();
			return errors;
		},
		{ getTargetsForAlert: () => TARGETS }
	);

	/* モーダル前チェック */
	$(CONST.IDS.MODAL)
		.off("before:open.myModal.chapter")
		.on("before:open.myModal.chapter", (e) => {
			const span = LOG.logStart("before:open.modal", { level: "INFO", duration: true });

			const errors = validateAndReflect("beforeOpenModal");
			const firstMsg = errors.no || errors.title || errors.sankouBooksViewId || "";

			if (firstMsg) {
				e.preventDefault();
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showNoFieldValid();
			form.showTitleFieldValid();
			form.showBookFieldValid();

			span.end();
			return true;
		});

	/* data-modal ボタン直押しの保険 */
	$(document)
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN, (e) => {
			const span = LOG.logStart("click.modalOpenButton", { level: "DEBUG", duration: true });

			const $btn = $(e.currentTarget);
			const target = $btn.attr(CONST.ATTR.DATA_MODAL) || "";
			if (target !== CONST.IDS.MODAL) {
				span.end();
				return;
			}

			const errors = validateAndReflect("modalOpenButton");
			const firstMsg = errors.no || errors.title || errors.sankouBooksViewId || "";

			if (firstMsg) {
				e.preventDefault();
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showNoFieldValid();
			form.showTitleFieldValid();
			form.showBookFieldValid();

			span.end();
			return true;
		});

	/* 最終 submit */
	form.$form
		.off(CONST.EVENT.SUBMIT)
		.on(CONST.EVENT.SUBMIT, (e) => {
			const span = LOG.logStart("submit.form", { level: "INFO", duration: true });

			const errors = validateAndReflect("submit");
			const firstMsg = errors.no || errors.title || errors.sankouBooksViewId || "";

			if (firstMsg) {
				e.preventDefault();
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
