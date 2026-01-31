// com.javastudy.components.users.internal.USER_UsersController.java
package com.javastudy.components.users.internal;

import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exception.util.param.MyExceptionParam;
import com.javastudy.components.users.api.service.UsersService;
import com.javastudy.config.security.AuthSessionRefresher;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.util.security.browser_guard.BrowserGuard;
import com.util.security.role.RoleUtil;
import com.util.type.MyConst;
import com.util.type.MyType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping(AppPath.USER_ROOT)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_UsersController {

	private final UsersService usersService;
	private final AuthSessionRefresher authSessionRefresher;
	private final ValidationMessageUtil msg;

	/* ===== アカウント設定：エントリ（/user/account/setting/browser-guard） ===== */

	/**
	 * アカウント設定画面へのエントリポイント。
	 *
	 * <p>
	 * POST /user/account/setting/browser-guard
	 * → Flash に BrowserGuard.OK を積んで
	 * → redirect:/user/account/setting
	 */
	@PostMapping(AppPath.ACCOUNT_SETTING_ENTRY)
	public String postAccountSettingEntry(final RedirectAttributes redirect) {
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_USER_ACCOUNT_SETTING;
	}

	/** 画面GET：アカウント設定。 */
	@GetMapping(AppPath.ACCOUNT_SETTING)
	public String getAccountSetting(
		final Model model,
		final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進むなど）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			// 不正フロー扱い：ユーザートップへ
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_USER;
		}

		// Form は ControllerAdvice 側で補充している想定なのでここでは何もしない
		// フローは正常として OK を積んでおく
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());

		return TempPath.ACCOUNT_SETTING;
	}

	/** 更新POST：ユーザー名変更（エラー時はPRG＋ERROR_MESSAGE） */
	@PostMapping(AppPath.ACCOUNT_UPDATE)
	@Transactional
	public String postAccountUpdate(
		@Valid @ModelAttribute(UsersFormParam.FORM) final USER_UsersForm form,
		final BindingResult result,
		final HttpServletRequest request,
		final HttpSession session,
		final Model model,
		final RedirectAttributes redirect) {

		// 1) バリデーション NG → PRG で GET に戻す
		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			// 入力維持が欲しければここで Flash に積む（現状コメントのまま）
			// redirect.addFlashAttribute(UsersFormParam.FORM, form);

			// フロー自体は正しいので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.R_USER_ACCOUNT_SETTING;
		}

		// 2) ユーザー名更新＋認証情報更新
		final String oldUsername = SecurityContextHolder.getContext()
			.getAuthentication()
			.getName();
		final String newUsername = form.toInputDto().username();

		this.usersService.updateUsername(form.toInputDto());
		this.authSessionRefresher.evictUser(oldUsername);
		this.authSessionRefresher.refreshCurrentUserPrincipal(
			newUsername,
			request.getSession(false));

		// 成功時：一応 OK を積んでホームへ
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_USER;
	}

	@PostMapping(AppPath.ACCOUNT_PASSWORD_RESET)
	@Transactional
	public String postAccountPasswordReset(
		final HttpServletRequest request,
		final HttpServletResponse response) {
		this.usersService.resetPassword(); // 実装に合わせて
		request.getSession().invalidate();
		return AppPath.R_LOGOUT;
	}

	/* ===== [private] START ===== */
	private String toOneLineMessage(final BindingResult result) {
		return result.getAllErrors().stream()
			.map(ObjectError::getDefaultMessage)
			.map(this.msg::getMessage)
			.distinct()
			.collect(Collectors.joining(" ／ "));
	}
	/* ===== [private] END ===== */

}
