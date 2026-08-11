/*
 * ADMIN_SankouBookColorController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_book_color.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - sankouBookColor の管理画面（設定ホーム／追加／更新）と削除・更新 POST の入口（最小）
 */

package com.javastudy.components.sankou_book_color.internal;

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

import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;
import com.javastudy.components.sankou_book_color.api.param.SankouBookColorObjParam;
import com.javastudy.components.sankou_book_color.api.service.SankouBookColorService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.browser_guard.BrowserGuard;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@AllArgsConstructor
public class ADMIN_SankouBookColorController {

	private final SankouBookColorService sankouBookColorService;
	private final ValidationMessageUtil msg;

	/* ===== [public/protected] START ===== */

	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR)
	public String getSetting(final Model model) {
		final List<ADMIN_SankouBookColorViewDto> viewList = this.sankouBookColorService
			.getAdminViewDtoList();
		model.addAttribute(SankouBookColorObjParam.VIEW_DTO_LIST, viewList);
		return TempPath.ADMIN_SANKOU_BOOK_COLOR;
	}

	/* ---------- 追加画面：エントリ（/insert/browser-guard） ---------- */

	/**
	 * 追加画面へのエントリポイント。
	 * POST(/admin/sankouBookColor/insert/browser-guard)
	 * → redirect:/admin/sankouBookColor/insert
	 */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_INSERT_ENTRY)
	public String postInsertEntry(final RedirectAttributes redirect) {
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_SANKOU_BOOK_COLOR_INSERT;
	}

	/** 追加画面（GET） */
	@GetMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_INSERT)
	public String getInsert(final Model model, final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			// 不正フロー → 設定ホームへ退避
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
		}

		// Form 未設定なら新規
		if (!model.containsAttribute(SankouBookColorFormParam.FORM)) {
			model.addAttribute(SankouBookColorFormParam.FORM, new ADMIN_SankouBookColorForm());
		}

		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_SANKOU_BOOK_COLOR_INSERT;
	}

	/** 追加（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_INSERT)
	public String postInsert(
		@ModelAttribute(SankouBookColorFormParam.FORM) @Valid final ADMIN_SankouBookColorForm form,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		// 0) BrowserGuard チェック（直叩き POST 防止）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
		}

		if (result.hasErrors()) {
			// エラーメッセージを Sticky（セッション）へ
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			// 入力維持
			redirect.addFlashAttribute(SankouBookColorFormParam.FORM, form);
			// フロー自体は正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			// PRG
			return AppPath.R_ADMIN_SANKOU_BOOK_COLOR_INSERT;
		}

		this.sankouBookColorService.create(form.toCreateInputDto());
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		// 設定ホーム側も通常フローとして OK に寄せておく
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
	}

	/* ---------- 更新画面：エントリ（/{id}/browser-guard） ---------- */

	/**
	 * 更新画面へのエントリポイント。
	 * POST(/admin/sankouBookColor/{sankou_book_color_view_id}/browser-guard)
	 * → redirect:/admin/sankouBookColor/{sankou_book_color_view_id}
	 */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_ID_ENTRY)
	public String postUpdateEntry(
		@PathVariable(AppPath.PARAM_SANKOU_BOOK_COLOR_VIEW_ID) final String viewId,
		final RedirectAttributes redirect) {

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rAdminSankouBookColorUpdate(viewId);
	}

	/** 更新画面（GET, 詳細） */
	@GetMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_ID)
	public String getUpdate(
		@PathVariable(AppPath.PARAM_SANKOU_BOOK_COLOR_VIEW_ID) final String viewId,
		final Model model,
		final RedirectAttributes redirect) {

		// BrowserGuard チェック
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
		}

		// Form 未設定（初回表示）のときだけ ViewDto から生成
		if (!model.containsAttribute(SankouBookColorFormParam.FORM)) {
			final ADMIN_SankouBookColorViewDto v = this.sankouBookColorService
				.getAdminViewDtoByViewId(viewId);
			model.addAttribute(
				SankouBookColorFormParam.FORM,
				ADMIN_SankouBookColorForm.fromViewDto(v));
		}
		// viewId（テンプレ用）★ここは COLOR 用のパラメータに修正
		model.addAttribute(AppPath.PARAM_SANKOU_BOOK_COLOR_VIEW_ID, viewId);

		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_SANKOU_BOOK_COLOR_UPDATE;
	}

	/** 更新（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_UPDATE)
	public String postUpdate(
		@PathVariable(AppPath.PARAM_SANKOU_BOOK_COLOR_VIEW_ID) final String viewId,
		@ModelAttribute(SankouBookColorFormParam.FORM) @Valid final ADMIN_SankouBookColorForm form,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		// 0) BrowserGuard チェック（直叩き POST 防止）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
		}

		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			redirect.addFlashAttribute(SankouBookColorFormParam.FORM, form);
			// フローは正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rAdminSankouBookColorUpdate(viewId);
		}

		this.sankouBookColorService.update(form.toUpdateInputDto(viewId));
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
	}

	/** 削除（POST）→ 設定ホーム */
	@PostMapping(AppPath.ADMIN_SANKOU_BOOK_COLOR_DELETE)
	public String postDelete(
		@PathVariable(AppPath.PARAM_SANKOU_BOOK_COLOR_VIEW_ID) final String viewId,
		final HttpSession session) {
		this.sankouBookColorService.deleteByViewId(viewId);
		session.removeAttribute(PropKey.ERROR_MESSAGE);
		return AppPath.R_ADMIN_SANKOU_BOOK_COLOR;
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** BindingResult → 代表メッセージ（重複排除 / 連結） */
	private String toOneLineMessage(final BindingResult result) {
		return result.getAllErrors().stream()
			.map(ObjectError::getDefaultMessage) // 例: "error.common.max"
			.map(this.msg::getMessage) // -> 実メッセージへ解決
			.distinct()
			.collect(Collectors.joining(" ／ "));
	}
	/* ===== [private] END ===== */
}
