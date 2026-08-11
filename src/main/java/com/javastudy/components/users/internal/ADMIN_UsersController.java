/*
 * ADMIN_UsersController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - Users の管理画面（設定ホーム／追加／更新）と削除・更新・パスワードリセット POST の入口（最小）
 *
 * 注意:
 * - import は明示指定（ワイルドカード禁止）
 * - Lombok を用いたコンストラクタ注入（方針に合わせて明示コンストラクタへ置換可）
 */

package com.javastudy.components.users.internal;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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

import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.components.authority.api.param.AuthorityObjParam;
import com.javastudy.components.authority.api.service.AuthorityService;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.users.api.param.UsersObjParam;
import com.javastudy.components.users.api.service.UsersService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.browser_guard.BrowserGuard;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;

@Controller
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@AllArgsConstructor
public class ADMIN_UsersController {

	private final UsersService usersService;
	private final AuthorityService authService;
	private final ValidationMessageUtil msg;

	/* ===== [public/protected] START ===== */

	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_USERS)
	public String getSetting(final Model model) {
		model.addAttribute(UsersObjParam.VIEW_DTO_LIST, this.usersService.getAdminViewDtoList());
		return TempPath.ADMIN_USERS;
	}

	/* ---------- 追加画面：エントリ（/insert/browser-guard） ---------- */

	/**
	 * 追加画面へのエントリポイント。
	 * POST(/admin/users/insert/browser-guard)
	 * → redirect:/admin/users/insert
	 */
	@PostMapping(AppPath.ADMIN_USERS_INSERT_ENTRY)
	public String postInsertEntry(final RedirectAttributes redirect) {
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_USERS_INSERT;
	}

	/** 追加画面（GET） */
	@GetMapping(AppPath.ADMIN_USERS_INSERT)
	public String getInsert(final Model model, final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute("alert-danger", true);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// Form 未設定なら新規
		if (!model.containsAttribute(UsersFormParam.FORM)) {
			model.addAttribute(UsersFormParam.FORM, new ADMIN_UsersForm());
		}
		// 権限セレクト用一覧
		model.addAttribute(AuthorityObjParam.VIEW_DTO_LIST, this.authService.getAdminViewDtoList());

		// フローは正常
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_USERS_INSERT;
	}

	/** 追加（POST）→ 設定ホームへ */
	@PostMapping(AppPath.ADMIN_USERS_INSERT)
	public String postInsert(
		@ModelAttribute(UsersFormParam.FORM) @Valid final ADMIN_UsersForm form,
		@ModelAttribute(AuthorityObjParam.VIEW_DTO_LIST) final List<ADMIN_AuthorityViewDto> authViewDtoList,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		if (result.hasErrors()) {
			// エラーメッセージを Sticky（セッション）へ
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			// 入力維持 + 権限一覧
			redirect.addFlashAttribute(UsersFormParam.FORM, form);
			redirect.addFlashAttribute(AuthorityObjParam.VIEW_DTO_LIST, authViewDtoList);
			// フロー自体は正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			// PRG
			return AppPath.R_ADMIN_USERS_INSERT;
		}

		this.usersService.create(form.toCreateInputDto());
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_USERS;
	}

	/* ---------- 更新画面：エントリ（/{id}/browser-guard） ---------- */

	/**
	 * 更新画面へのエントリポイント。
	 * POST(/admin/users/{users_view_id}/browser-guard)
	 * → redirect:/admin/users/{users_view_id}
	 */
	@PostMapping(AppPath.ADMIN_USERS_ID_ENTRY)
	public String postUpdateEntry(
		@PathVariable(AppPath.PARAM_USERS_VIEW_ID) final String viewId,
		final RedirectAttributes redirect) {

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rAdminUsersUpdate(viewId);
	}

	/** 更新画面（GET, 詳細） */
	@GetMapping(AppPath.ADMIN_USERS_ID)
	public String getUpdate(
		@PathVariable(AppPath.PARAM_USERS_VIEW_ID) final String viewId,
		final Model model,
		final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute("alert-danger", true);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// Form 未設定（初回表示）のときだけ ViewDto から生成
		if (!model.containsAttribute(UsersFormParam.FORM)) {
			final ADMIN_UsersViewDto v = this.usersService.getAdminViewDtoByViewId(viewId);
			model.addAttribute(UsersFormParam.FORM, ADMIN_UsersForm.fromViewDto(v));
		}
		// 権限セレクト用一覧
		model.addAttribute(AuthorityObjParam.VIEW_DTO_LIST, this.authService.getAdminViewDtoList());
		// テンプレ用 viewId
		model.addAttribute(AppPath.PARAM_USERS_VIEW_ID, viewId);

		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_USERS_UPDATE;
	}

	/** 更新（POST）→ 設定ホームへ */
	@PostMapping(AppPath.ADMIN_USERS_UPDATE)
	public String postUpdate(
		@PathVariable(AppPath.PARAM_USERS_VIEW_ID) final String viewId,
		@ModelAttribute(UsersFormParam.FORM) @Valid final ADMIN_UsersForm form,
		@ModelAttribute(AuthorityObjParam.VIEW_DTO_LIST) final List<ADMIN_AuthorityViewDto> authViewDtoList,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			redirect.addFlashAttribute(UsersFormParam.FORM, form);
			redirect.addFlashAttribute(AuthorityObjParam.VIEW_DTO_LIST, authViewDtoList);
			// フローは正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rAdminUsersUpdate(viewId);
		}

		this.usersService.update(form.toUpdateInputDto(viewId));
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_USERS;
	}

	/** 削除（POST）→ 設定ホームへ */
	@PostMapping(AppPath.ADMIN_USERS_DELETE)
	public String postDelete(
		@PathVariable(AppPath.PARAM_USERS_VIEW_ID) final String viewId,
		final HttpSession session) {
		this.usersService.deleteByViewId(viewId);
		session.removeAttribute(PropKey.ERROR_MESSAGE);
		return AppPath.R_ADMIN_USERS;
	}

	/** パスワードリセット（POST）→ 設定ホームへ */
	@PostMapping(AppPath.ADMIN_USERS_PASSWORD_RESET)
	public String postPasswordReset(
		@PathVariable(AppPath.PARAM_USERS_VIEW_ID) final String viewId) {
		this.usersService.resetPassword(viewId); // 実装に合わせて
		return AppPath.R_ADMIN_USERS;
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
