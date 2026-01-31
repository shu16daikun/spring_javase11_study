// com.javastudy.config.SecurityConfig
package com.javastudy.config;

import com.javastudy.components.login.login.api.param.LoginFormParam;
import com.javastudy.util.path.AppPath;
import com.login.components.authority.api.domain.MyAuthorityEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** Spring Security 構成（フォームログイン／URL認可）。 “画面に見せるもの”のルーティングを定義し、内部ロジックは出さない。 */
@Configuration
@EnableWebSecurity
public class SecurityConfig { // public 非final（AOP方針）

	/* ===== [private] START ===== */
	/* 役割名（ROLE_なしのシステム表記） */
	private static final String roleAdmin = MyAuthorityEnum.ADMIN.getSystemName();
	private static final String roleUser = MyAuthorityEnum.USER.getSystemName();

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/**
	 * HTTPセキュリティの主設定（認可ルール／フォームログイン／ログアウト）。
	 *
	 * @param http
	 *            HttpSecurity ビルダー
	 * @return SecurityFilterChain（適用済みチェイン）
	 * @throws Exception
	 *             ビルド時の例外
	 */
	@Bean
	protected SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		http
			/* 認可ルール：静的/ログインは誰でも、/admin は ADMIN、/user は USER */
			.authorizeHttpRequests(
				auth -> auth.requestMatchers(
					AppPath.LOGIN,
					AppPath.LOGIN_ERROR_ALL,
					AppPath.TEST_ALL,
					AppPath.CSS_ALL,
					AppPath.JS_ALL,
					AppPath.IMG_ALL,
					AppPath.PSFM_ALL,
					AppPath.WEBJARS_ALL,
					AppPath.FAVICON)
					.permitAll()
					.requestMatchers(AppPath.ADMIN_ALL)
					.hasRole(roleAdmin)
					.requestMatchers(AppPath.USER_ALL)
					.hasAnyRole(roleUser)
					.anyRequest()
					.authenticated())
			/* フォームログイン：パラメータ名は LoginFormParam 準拠 */
			.formLogin(
				form -> form.loginPage(AppPath.LOGIN)
					.loginProcessingUrl(AppPath.LOGIN)
					.usernameParameter(LoginFormParam.USERNAME)
					.passwordParameter(LoginFormParam.PASSWORD)
					.defaultSuccessUrl(AppPath.LOGIN_SUCCESS, true)
					.failureUrl(AppPath.LOGIN_FAILURE)
					.permitAll())
			/* ログアウト：URL/成功遷移のみを画面公開 */
			.logout(
				logout -> logout
					.logoutUrl(AppPath.LOGOUT)
					.logoutSuccessUrl(AppPath.LOGOUT_SUCCESS)
					.permitAll());
		return http.build();
	}

	/**
	 * パスワードエンコーダ（BCrypt）。
	 *
	 * @return PasswordEncoder
	 */
	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 認証マネージャ（AuthenticationConfiguration から委譲取得）。
	 *
	 * @param authConfig
	 *            認証設定
	 * @return AuthenticationManager
	 * @throws Exception
	 *             取得失敗時
	 */
	@Bean
	protected AuthenticationManager authenticationManager(
		final AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	/* ===== [public/protected] END ===== */
}
