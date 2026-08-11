// LoginController.java
package com.javastudy.components.login.login.internal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.javastudy.components.login.login.api.param.LoginFormParam;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.login.components.authority.api.domain.MyAuthorityEnum;
import com.login.components.authority.api.service.MyAuthorityService;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyUsersService;
import com.my.util.security.browser_guard.BrowserGuard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * ログイン／ログアウトおよびログイン後の遷移制御。
 *
 * <p>
 * “画面に見せるもの”：Model へのフォーム投入、redirect パス選択。
 *
 * <p>
 * “ログ専用”：本クラスではログ出力しない（ControllerAdvice へ集約）。
 */
@Controller
@AllArgsConstructor
public class LoginController {

	/* ===== [private] START ===== */
	private final MyUsersService loginUserService;
	private final MyAuthorityService loginAuthService;
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	/**
	 * ログイン画面表示（空フォームを渡す）。
	 */
	@GetMapping(AppPath.LOGIN)
	public String getLogin(
		final Authentication authentication,
		final Model model,
		final RedirectAttributes redirect,
		final HttpServletRequest request,
		final HttpServletResponse response) {
		if (this.loginUserService.isLoggedIn(authentication)) {
			redirect.addFlashAttribute(
				BrowserGuard.PARAM,
				BrowserGuard.invalidFlow());

			// 想定外：どのロールにも当たらない場合はいったんログアウトしてログイン画面へ
			this.loginUserService.logout(request, response, authentication);
			return AppPath.R_LOGIN;
		}
		model.addAttribute(LoginFormParam.FORM, new LoginForm());
		return TempPath.LOGIN;
	}

	/**
	 * ログイン成功後の遷移先を決定。
	 *
	 * <ul>
	 * <li>初回ログイン: パスワード設定へ
	 * <li>ADMIN 権限: 管理トップへ
	 * <li>それ以外: ユーザートップへ
	 * </ul>
	 */
	@GetMapping(AppPath.LOGIN_SUCCESS)
	public String getLoginSuccess(
		final Authentication authentication,
		final RedirectAttributes redirectAttributes,
		final HttpServletRequest request,
		final HttpServletResponse response) {

		// ★想定外アクセス（セッション切れ or URL直叩き）
		if (this.loginUserService.isNotLoggedIn(authentication)) {
			// 念のためセッション＆SecurityContext を破棄
			this.loginUserService.logout(request, response, authentication);

			// ログイン画面側でモーダル表示させたいので BrowserGuard を FlashAttribute に積む
			redirectAttributes.addFlashAttribute(
				BrowserGuard.PARAM,
				BrowserGuard.sessionTimeout());
			return AppPath.R_LOGIN; // e.g. "redirect:/login"
		}

		final MyUsersViewDto loginUser = this.loginUserService.getLoginUser();

		// 正常系：ここで明示的に OK を積んでもいいし、
		// GlobalBrowserGuardAdvice に任せてもOK（好み）
		redirectAttributes.addFlashAttribute(
			BrowserGuard.PARAM,
			BrowserGuard.ok());

		if (loginUser.isFirstLogin()) {
			return AppPath.R_PASS_SET;
		}
		return this.loginAuthService.hasRole(MyAuthorityEnum.ADMIN, loginUser)
			? AppPath.R_ADMIN
			: AppPath.R_USER;
	}

	/** ログアウト処理。認証の有無にかかわらずサービス側の共通処理へ委譲。 */
	@PostMapping(AppPath.LOGOUT)
	public String logout(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Authentication authentication) {

		this.loginUserService.logout(request, response, authentication);
		return AppPath.R_LOGOUT;
	}

	/* ===== [public/protected] END ===== */
}
