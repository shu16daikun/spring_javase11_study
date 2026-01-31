// PasswordSetController.java
package com.javastudy.components.login.password_set.internal;

import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.login.components.authority.api.domain.MyAuthorityEnum;
import com.login.components.authority.api.service.MyAuthorityService;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyPasswordSetService;
import com.login.components.user.api.service.MyUsersService;
import com.util.security.browser_guard.BrowserGuard;
import com.util.type.MyType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * パスワード設定画面の表示／確定処理（PRG採用）。
 *
 * <p>
 * “画面に見せるもの”：Form・検証メッセージ・結果メッセージ。
 *
 * <p>
 * “ログ専用”：ログは Advice 側に集約。本コントローラでは行わない。
 */
@Controller
@AllArgsConstructor
public class PasswordSetController {

	/* ===== [private] START ===== */
	private final MyUsersService loginUserService;
	private final MyAuthorityService loginAuthService;
	private final MyPasswordSetService passSetService;
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	/** GET：パスワード設定画面を表示。 */
	@GetMapping(AppPath.PASS_SET)
	public String getPasswordSet(
		final Authentication authentication,
		final Model model,
		final RedirectAttributes redirect,
		final HttpServletRequest request,
		final HttpServletResponse response) {

		// 1) 未ログイン → セッション切れ
		if (this.loginUserService.isNotLoggedIn(authentication)) {
			// 念のためログアウト処理（セッション＆SecurityContext 破棄）
			this.loginUserService.logout(request, response, authentication);

			redirect.addFlashAttribute(
				BrowserGuard.PARAM,
				BrowserGuard.sessionTimeout());
			return AppPath.R_LOGIN;
		}

		final MyUsersViewDto loginUser = this.loginUserService.getLoginUser();

		// 2) browserGuard の Code を model から取得
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);

		// 「loginSuccess から OK が飛んできている」＋ firstLogin=true が前提
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok()) || !loginUser.isFirstLogin()) {
			redirect.addFlashAttribute(
				BrowserGuard.PARAM,
				BrowserGuard.invalidFlow());

			// 想定外：どのロールにも当たらない場合はいったんログアウトしてログイン画面へ
			this.loginUserService.logout(request, response, authentication);
			return AppPath.R_LOGIN;
		}

		// 3) 正常表示
		if (!model.containsAttribute(PasswordSetFormParam.FORM)) {
			model.addAttribute(PasswordSetFormParam.FORM, new PasswordSetForm());
		}
		// 念のため OK に寄せておく（Flash→Model の値をそのまま使ってもOK）
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());

		return TempPath.PASS_SET;
	}

	/**
	 * POST：パスワード更新。失敗時は PRG で GET に戻す。
	 *
	 * <ul>
	 * <li>検証エラー時：Form/BindingResult/エラーメッセージを Flash へ載せてリダイレクト
	 * <li>成功時：権限で遷移分岐（ADMIN→/admin、USER→/user）
	 * </ul>
	 */
	@PostMapping(AppPath.PASS_SET)
	public String postPasswordSet(
		@Valid @ModelAttribute(PasswordSetFormParam.FORM) final PasswordSetForm form,
		final BindingResult result,
		final RedirectAttributes redirect,
		final Authentication authentication,
		final Model model,
		final HttpServletRequest request,
		final HttpServletResponse response) {

		// 0) 未ログイン → セッション切れ
		if (this.loginUserService.isNotLoggedIn(authentication)) {
			// 念のためログアウト
			this.loginUserService.logout(request, response, authentication);

			redirect.addFlashAttribute(
				BrowserGuard.PARAM,
				BrowserGuard.sessionTimeout());
			return AppPath.R_LOGIN;
		}
		final MyUsersViewDto loginUser = this.loginUserService.getLoginUser();

		// 1) バリデーション NG → PRG で GET に戻す（フローは OK とみなす）
		if (result.hasErrors()) {
			redirect.addFlashAttribute(PasswordSetFormParam.FORM, form);
			redirect.addFlashAttribute(
				BindingResult.MODEL_KEY_PREFIX + PasswordSetFormParam.FORM,
				result);

			final String joined = result.getAllErrors().stream()
				.map(err -> err.getDefaultMessage())
				.collect(Collectors.joining("\\n"));
			redirect.addFlashAttribute(PropKey.ERROR_MESSAGE, joined);

			redirect.addFlashAttribute(
				BrowserGuard.PARAM,
				BrowserGuard.ok());
			return AppPath.R_PASS_SET;
		}

		// 2) パスワード更新
		this.passSetService.updatePasswordEncode(form.toInputDto());

		// 3) 成功時：OK＋メッセージをホーム側へ
		redirect.addFlashAttribute(
			BrowserGuard.PARAM,
			BrowserGuard.ok());

		if (this.loginAuthService.hasRole(MyAuthorityEnum.ADMIN, loginUser)) {
			return AppPath.R_ADMIN;
		}
		return AppPath.R_USER;
	}

	/* ===== [public/protected] END ===== */
}
