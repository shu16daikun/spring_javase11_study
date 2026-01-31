/*
 * ADMIN_SankouBooksController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - SankouBooks の管理画面（設定ホーム／追加／更新）と削除・更新 POST の入口（最小）
 */

package com.javastudy.components.sankou_books.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exception.util.param.MyExceptionParam;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;
import com.javastudy.components.sankou_book_color.api.param.SankouBookColorObjParam;
import com.javastudy.components.sankou_book_color.api.service.SankouBookColorService;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.util.security.browser_guard.BrowserGuard;
import com.util.security.role.RoleUtil;
import com.util.type.MyConst;
import com.util.type.MyType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@AllArgsConstructor
public class ADMIN_SankouBooksController {

	private final SankouBooksService sankouBooksService;
	private final SankouBookColorService colorService;
	private final ValidationMessageUtil msg;

	/* ===== [public/protected] START ===== */

	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_SANKOU_BOOKS)
	public String getSetting(final Model model) {
		final List<ADMIN_SankouBooksViewDto> viewList = this.sankouBooksService
			.getAdminViewDtoList();
		model.addAttribute(SankouBooksObjParam.VIEW_DTO_LIST, viewList);
		return TempPath.ADMIN_SANKOU_BOOKS;
	}

	/* ---------- 追加画面：エントリ（/insert/browser-guard） ---------- */

	/**
	 * 追加画面へのエントリポイント。
	 * POST(/admin/sankouBooks/insert/browser-guard)
	 * → redirect:/admin/sankouBooks/insert
	 */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOKS_INSERT_ENTRY)
	public String postInsertEntry(final RedirectAttributes redirect) {
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_SANKOU_BOOKS_INSERT;
	}

	/** 追加画面（GET） */
	@GetMapping(AppPath.ADMIN_SANKOU_BOOKS_INSERT)
	public String getInsert(final Model model, final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// Form 未設定なら新規
		if (!model.containsAttribute(SankouBooksFormParam.FORM)) {
			model.addAttribute(SankouBooksFormParam.FORM, new ADMIN_SankouBooksForm());
		}
		// カラーセレクト用一覧
		model.addAttribute(
			SankouBookColorObjParam.VIEW_DTO_LIST,
			this.colorService.getAdminViewDtoList());

		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_SANKOU_BOOKS_INSERT;
	}

	/** 追加（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOKS_INSERT)
	public String postInsert(
		@ModelAttribute(SankouBooksFormParam.FORM) @Valid final ADMIN_SankouBooksForm form,
		@ModelAttribute(SankouBookColorObjParam.VIEW_DTO_LIST) final List<ADMIN_SankouBookColorViewDto> colorViewDtoList,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			redirect.addFlashAttribute(SankouBooksFormParam.FORM, form);
			// セレクト再描画用にカラー一覧もPRGへ載せる
			redirect.addFlashAttribute(
				SankouBookColorObjParam.VIEW_DTO_LIST,
				colorViewDtoList);
			// フロー自体は正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.R_ADMIN_SANKOU_BOOKS_INSERT;
		}

		this.sankouBooksService.create(form.toCreateInputDto());
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_SANKOU_BOOKS;
	}

	/* ---------- 更新画面：エントリ（/{id}/browser-guard） ---------- */

	/**
	 * 更新画面へのエントリポイント。
	 * POST(/admin/sankouBooks/{sankou_books_view_id}/browser-guard)
	 * → redirect:/admin/sankouBooks/{sankou_books_view_id}
	 */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOKS_ID_ENTRY)
	public String postUpdateEntry(
		@PathVariable(AppPath.PARAM_SANKOU_BOOKS_VIEW_ID) final String viewId,
		final RedirectAttributes redirect) {

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rAdminSankouBooksUpdate(viewId);
	}

	/** 更新画面（GET, 詳細） */
	@GetMapping(AppPath.ADMIN_SANKOU_BOOKS_ID)
	public String getUpdate(
		@PathVariable(AppPath.PARAM_SANKOU_BOOKS_VIEW_ID) final String viewId,
		final Model model,
		final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// Form 未設定（初回表示）のときだけ ViewDto から生成
		if (!model.containsAttribute(SankouBooksFormParam.FORM)) {
			final ADMIN_SankouBooksViewDto v = this.sankouBooksService
				.getAdminViewDtoByViewId(viewId);
			model.addAttribute(SankouBooksFormParam.FORM, ADMIN_SankouBooksForm.fromViewDto(v));
		}
		// カラーセレクト用一覧
		model.addAttribute(
			SankouBookColorObjParam.VIEW_DTO_LIST,
			this.colorService.getAdminViewDtoList());
		// テンプレ用 viewId
		model.addAttribute(AppPath.PARAM_SANKOU_BOOKS_VIEW_ID, viewId);

		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_SANKOU_BOOKS_UPDATE;
	}

	/** 更新（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOKS_UPDATE)
	public String postUpdate(
		@PathVariable(AppPath.PARAM_SANKOU_BOOKS_VIEW_ID) final String viewId,
		@ModelAttribute(SankouBooksFormParam.FORM) @Valid final ADMIN_SankouBooksForm form,
		@ModelAttribute(SankouBookColorObjParam.VIEW_DTO_LIST) final List<ADMIN_SankouBookColorViewDto> colorViewDtoList,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			redirect.addFlashAttribute(SankouBooksFormParam.FORM, form);
			// 再描画時にセレクトが空にならないようカラー一覧も付与
			redirect.addFlashAttribute(
				SankouBookColorObjParam.VIEW_DTO_LIST,
				colorViewDtoList);
			// フローは正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rAdminSankouBooksUpdate(viewId);
		}

		this.sankouBooksService.update(form.toUpdateInputDto(viewId));
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_SANKOU_BOOKS;
	}

	/** 削除（POST）→ 設定ホーム */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOKS_DELETE)
	public String postDelete(
		@PathVariable(AppPath.PARAM_SANKOU_BOOKS_VIEW_ID) final String viewId,
		final HttpSession session) {
		this.sankouBooksService.deleteByViewId(viewId);
		session.removeAttribute(PropKey.ERROR_MESSAGE);
		return AppPath.R_ADMIN_SANKOU_BOOKS;
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** BindingResult → 代表メッセージ（重複排除 / 連結） */
	private String toOneLineMessage(final BindingResult result) {
		return result.getAllErrors().stream()
			.map(ObjectError::getDefaultMessage)
			.map(this.msg::getMessage)
			.distinct()
			.collect(Collectors.joining(" ／ "));
	}
	/* ===== [private] END ===== */
}
