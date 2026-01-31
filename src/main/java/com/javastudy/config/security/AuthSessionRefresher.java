package com.javastudy.config.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * セッション中の認証情報を“最新状態”へ整合させるユーティリティ。
 *
 * <ul>
 * <li>UserCache があれば対象ユーザーを明示的に退避（エビクト）
 * <li>UserDetails を再読込して SecurityContext を差し替え、必要に応じて HttpSession へ反映
 * </ul>
 *
 * <p>
 * “画面に見せるもの”は持たず、ログ／セキュリティコンテキスト更新専用。
 */
@Component
@RequiredArgsConstructor
public class AuthSessionRefresher { // public 非final（AOP方針どおり）

	/* ===== [private] START ===== */
	/** UserCache は任意依存（存在しなければ何もしない） */
	private final ObjectProvider<UserCache> userCache;

	/** ユーザー情報再読込用 */
	private final UserDetailsService userDetailsService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/**
	 * 指定ユーザーの UserCache を無効化する（存在時のみ）。
	 *
	 * <p>
	 * Cache が未設定の場合は何もしない（noop）。
	 *
	 * @param oldUsername
	 *            キャッシュを破棄したい“旧ユーザー名”
	 */
	public void evictUser(final String oldUsername) {
		final UserCache cache = userCache.getIfAvailable();
		if (cache != null) {
			cache.removeUserFromCache(oldUsername);
		}
		// 画面出力なし（ログ専用）
	}

	/**
	 * 現在の認証主体（Principal）を新ユーザー名で再構成し、SecurityContext と HttpSession を更新する。
	 *
	 * <p>
	 * UsernamePasswordAuthenticationToken#authenticated(...) で差し替え。
	 *
	 * @param newUsername
	 *            差し替え後のユーザー名
	 * @param session
	 *            現在の HTTP セッション（null 可：null 時はセッション未反映）
	 */
	public void refreshCurrentUserPrincipal(final String newUsername, final HttpSession session) {
		// UserDetails を再読込
		var details = userDetailsService.loadUserByUsername(newUsername);
		// 認証済みトークンを再生成
		var auth = org.springframework.security.authentication.UsernamePasswordAuthenticationToken
			.authenticated(details, details.getPassword(), details.getAuthorities());
		// SecurityContext を差し替え
		SecurityContextHolder.getContext().setAuthentication(auth);
		// セッション（画面側状態維持）へ反映（必要時）
		if (session != null) {
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		}
	}
	/* ===== [public/protected] END ===== */
}
