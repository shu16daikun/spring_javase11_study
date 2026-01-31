// com.javastudy.components.authority.internal.ADMIN_AuthorityController.java
package com.javastudy.components.authority.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exception.util.param.MyExceptionParam;
import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.components.authority.api.param.AuthorityObjParam;
import com.javastudy.components.authority.api.service.AuthorityService;
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
public class ADMIN_AuthorityController {

	/* ===== [private] START ===== */
	private final AuthorityService authorityService;
	private final ValidationMessageUtil msg;
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_AUTHORITY)
	public String getSetting(final Model model) {
		final List<ADMIN_AuthorityViewDto> authViewDtoList = this.authorityService
			.getAdminViewDtoList();
		model.addAttribute(AuthorityObjParam.VIEW_DTO_LIST, authViewDtoList);
		return TempPath.ADMIN_AUTHORITY;
	}

	/* ========== [INSERT：BrowserGuard エントリ] ========== */

	/**
	 * 追加画面へのエントリポイント。
	 * POST(/admin/authority/insert/browser-guard) →
	 * redirect:/admin/authority/insert
	 */
	@PostMapping(AppPath.ADMIN_AUTHORITY_INSERT_ENTRY)
	public String postInsertEntry(final RedirectAttributes redirect) {
		// 正常フローとして OK を付与
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_AUTHORITY_INSERT;
	}

	/** 追加画面（GET） */
	@GetMapping(AppPath.ADMIN_AUTHORITY_INSERT)
	public String getInsert(final Model model, final RedirectAttributes redirect) {

		// 1) BrowserGuardCode を取得
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);

		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// 3) 正常表示：PRG で Form が来ていなければ新規生成
		if (!model.containsAttribute(AuthorityFormParam.FORM)) {
			model.addAttribute(AuthorityFormParam.FORM, new ADMIN_AuthorityForm());
		}
		// フロント(JS) 用にも OK を載せておく
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());

		return TempPath.ADMIN_AUTHORITY_INSERT;
	}

	/** 追加（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_AUTHORITY_INSERT)
	@Transactional
	public String postInsert(
		@ModelAttribute(AuthorityFormParam.FORM) @Valid final ADMIN_AuthorityForm form,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		// 1) バリデーション NG → PRG で insert 画面へ戻す（フロー自体は OK）
		if (result.hasErrors()) {
			// エラーメッセージを Sticky（セッション）へ
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			// 入力維持
			redirect.addFlashAttribute(AuthorityFormParam.FORM, form);
			// BrowserGuard 的には「正しいフロー上のエラー」なので OK のまま
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			// PRG
			return AppPath.R_ADMIN_AUTHORITY_INSERT;
		}

		// 2) 登録
		this.authorityService.create(form.toCreateInputDto());

		// 3) エラーメッセージ粘着を解除
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		// 4) 正常完了として OK をホームへ
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_AUTHORITY;
	}

	/* ========== [UPDATE：BrowserGuard エントリ] ========== */

	/**
	 * 更新画面へのエントリポイント。
	 * POST(/admin/authority/{id}/browser-guard) →
	 * redirect:/admin/authority/{id}
	 */
	@PostMapping(AppPath.ADMIN_AUTHORITY_ID_ENTRY)
	public String postUpdateEntry(
		@PathVariable(AppPath.PARAM_AUTHORITY_VIEW_ID) final String viewId,
		final RedirectAttributes redirect) {

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rAdminAuthorityUpdate(viewId);
	}

	/** 更新画面（GET, 詳細） */
	@GetMapping(AppPath.ADMIN_AUTHORITY_ID)
	public String getUpdate(
		@PathVariable(AppPath.PARAM_AUTHORITY_VIEW_ID) final String viewId,
		final Model model,
		final RedirectAttributes redirect) {

		// 1) BrowserGuardCode を取得
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);

		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// 3) 正常表示：PRG で Form が来ていなければサービスから取得
		if (!model.containsAttribute(AuthorityFormParam.FORM)) {
			final ADMIN_AuthorityViewDto v = this.authorityService.getAdminViewDtoByViewId(viewId);
			model.addAttribute(AuthorityFormParam.FORM, ADMIN_AuthorityForm.fromViewDto(v));
		}
		// hidden / th:field 用に viewId も載せる
		model.addAttribute(AppPath.PARAM_AUTHORITY_VIEW_ID, viewId);
		// フロント用
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());

		return TempPath.ADMIN_AUTHORITY_UPDATE;
	}

	/** 更新（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_AUTHORITY_UPDATE)
	@Transactional
	public String postUpdate(
		@PathVariable(AppPath.PARAM_AUTHORITY_VIEW_ID) final String viewId,
		@ModelAttribute(AuthorityFormParam.FORM) @Valid final ADMIN_AuthorityForm form,
		final BindingResult result,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		// 1) バリデーション NG → PRG で更新画面へ戻す（フローは OK）
		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			redirect.addFlashAttribute(AuthorityFormParam.FORM, form);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rAdminAuthorityUpdate(viewId);
		}

		// 2) 更新
		this.authorityService.update(form.toUpdateInputDto(viewId));

		// 3) エラーメッセージ粘着解除
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		// 4) 正常完了として OK をホームへ
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_AUTHORITY;
	}

	/** 削除（POST）→ 設定ホーム */
	@PostMapping(AppPath.ADMIN_AUTHORITY_DELETE)
	@Transactional
	public String postDelete(
		@PathVariable(AppPath.PARAM_AUTHORITY_VIEW_ID) final String viewId,
		final HttpSession session,
		final RedirectAttributes redirect) {

		this.authorityService.deleteByViewId(viewId);
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		// これも通常フローとして OK に寄せておく
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_AUTHORITY;
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
