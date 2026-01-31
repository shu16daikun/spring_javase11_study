// /js/main-contents/admin/users/insert-update/controller.js
import { setValidationInputChange, setValidation } from "/psfm/js/fragment/validation.js";
import { setAlertDanger } from "/psfm/js/fragment/alert.js";
import { setFormFloatLabel } from "/psfm/js/fragment/form.js";
import { appendCsrfToForm } from "/js/util/csrf.js";
import { setCommonReady } from "/js/main-contents/admin/common.js";

import { getLoger } from "/psfm/js/common/loger.js";

import { CONST } from "./const.js";
import { UsersInsertUpdateForm } from "./form.js";
import { UsersInsertUpdateService } from "./service.js";

const LOG = getLoger("admin.users.insertUpdate");

/* 共通初期化 */
setCommonReady(() => {
	const initSpan = LOG.logStart("UsersInsertUpdateController#init", { level: "INFO", duration: true });

	/* 初期UI */
	setFormFloatLabel();
	setAlertDanger();

	const form = new UsersInsertUpdateForm($(CONST.SELECTOR.FORM));
	const service = new UsersInsertUpdateService(CONST.MESSAGES());
	const TARGETS = CONST.VALIDATION_TARGETS;

	/* 空の .my-invalid に行高確保 */
	form.$form.find(CONST.SELECTOR.INVALID).each(function() {
		const $el = $(this);
		const txt = String($el.attr(CONST.ATTR.DATA_TEXT) || "").trim();
		if (txt.length === 0) $el.text("\u00A0").css("visibility", "hidden");
	});

	/* 権限 select 変更 → 再検証 */
	$(document)
		.off("change.adminUsers", CONST.SELECTOR.AUTH_SELECT)
		.on("change.adminUsers", CONST.SELECTOR.AUTH_SELECT, () => {
			const span = LOG.logStart("UsersInsertUpdateController#changeAuthority", { level: "DEBUG", duration: true });

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			if (errors.authorityViewId) {
				form.showErrorMessage(errors.authorityViewId);
				form.showAuthorityFieldError(errors.authorityViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
				form.showAuthorityFieldValid();
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
			const span = LOG.logStart("UsersInsertUpdateController#validateInputChange", { level: "TRACE", duration: true });

			const v = {
				username: String(values.username ?? ""),
				authorityViewId: String(values["authorityViewId"] ?? ""),
			};
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			// ユーザー名
			if (errors.username) {
				form.showErrorMessage(errors.username);
				form.showUsernameFieldError(errors.username);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.hideErrorMessage();
				form.showUsernameFieldValid();
			}

			// 権限
			if (errors.authorityViewId) {
				form.showErrorMessage(errors.authorityViewId);
				form.showAuthorityFieldError(errors.authorityViewId);
				form.$form.find(CONST.SELECTOR.INVALID).css("visibility", "visible");
			} else {
				form.showAuthorityFieldValid();
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

	/* モーダル前チェック */
	$(CONST.IDS.MODAL)
		.off("before:open.myModal.users")
		.on("before:open.myModal.users", (e) => {
			const span = LOG.logStart("UsersInsertUpdateController#beforeOpenModal", { level: "DEBUG", duration: true });

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg = errors.username || errors.authorityViewId || "";
			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.username) form.showUsernameFieldError(errors.username);
				if (errors.authorityViewId) form.showAuthorityFieldError(errors.authorityViewId);
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showUsernameFieldValid();
			form.showAuthorityFieldValid();
			span.end();
			return true;
		});

	/* data-modal ボタン直押しの保険 */
	$(document)
		.off(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN)
		.on(CONST.EVENT.CLICK, CONST.SELECTOR.MODAL_OPEN, (e) => {
			const span = LOG.logStart("UsersInsertUpdateController#clickModalOpen", { level: "DEBUG", duration: true });

			const $btn = $(e.currentTarget);
			const target = $btn.attr(CONST.ATTR.DATA_MODAL) || "";
			if (target !== CONST.IDS.MODAL) {
				span.end();
				return;
			}

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg = errors.username || errors.authorityViewId || "";
			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.username) form.showUsernameFieldError(errors.username);
				if (errors.authorityViewId) form.showAuthorityFieldError(errors.authorityViewId);
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			form.hideErrorMessage();
			form.showUsernameFieldValid();
			form.showAuthorityFieldValid();
			span.end();
			return true;
		});

	/* 最終 submit */
	form.$form
		.off(CONST.EVENT.SUBMIT)
		.on(CONST.EVENT.SUBMIT, (e) => {
			const span = LOG.logStart("UsersInsertUpdateController#submit", { level: "DEBUG", duration: true });

			const v = form.getValues();
			const errors = service.validate(v) || {};
			setValidation(errors, TARGETS);

			const firstMsg = errors.username || errors.authorityViewId || "";
			if (firstMsg) {
				e.preventDefault();
				form.showErrorMessage(firstMsg);
				if (errors.username) form.showUsernameFieldError(errors.username);
				if (errors.authorityViewId) form.showAuthorityFieldError(errors.authorityViewId);
				form.focusFirstError(errors);
				span.end();
				return false;
			}

			appendCsrfToForm(form.$form[0]);
			span.end();
			return true;
		});

	/* パスワードリセット（update画面用の保険） */
	$("#pass-reset-form")
		.off(CONST.EVENT.SUBMIT)
		.on(CONST.EVENT.SUBMIT, function() {
			const span = LOG.logStart("UsersInsertUpdateController#passResetSubmit", { level: "DEBUG", duration: true });
			appendCsrfToForm(this);
			span.end();
			return true;
		});

	initSpan.end();
});
