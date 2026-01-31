// src/main/java/com/javastudy/config/web/WebMvcConfig.java
package com.javastudy.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.javastudy.util.path.AppPath;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final NoStoreInterceptor noStoreInterceptor;

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(this.noStoreInterceptor)
			// ===== ログイン系 =====
			// ログイン画面
			.addPathPatterns(AppPath.LOGIN)
			// 初回パスワード設定画面
			.addPathPatterns(AppPath.PASS_SET)

			// ===== ユーザー側 =====
			// アカウント設定画面 (/user/account/setting)
			.addPathPatterns(AppPath.USER_ACCOUNT_SETTING)
			// 章NO 画面（解答画面）：/user/sankouBooks/{book_view_id}/{chapter_no}
			//   → /user/sankouBooks/*/* として2セグメントぶんだけマッチさせる
			.addPathPatterns(AppPath.USER_SANKOU_BOOKS + "/*/*")

			// ===== 管理者側（INSERT & {viewId} 詳細画面） =====
			// 権限
			.addPathPatterns(AppPath.ADMIN_AUTHORITY_INSERT)
			.addPathPatterns(AppPath.ADMIN_AUTHORITY + "/*")

			// ユーザー
			.addPathPatterns(AppPath.ADMIN_USERS_INSERT)
			.addPathPatterns(AppPath.ADMIN_USERS + "/*")

			// 参考書カラー
			.addPathPatterns(AppPath.ADMIN_SANKOU_BOOK_COLOR_INSERT)
			.addPathPatterns(AppPath.ADMIN_SANKOU_BOOK_COLOR + "/*")

			// 参考書
			.addPathPatterns(AppPath.ADMIN_SANKOU_BOOKS_INSERT)
			.addPathPatterns(AppPath.ADMIN_SANKOU_BOOKS + "/*")

			// 章
			.addPathPatterns(AppPath.ADMIN_CHAPTER_INSERT)
			.addPathPatterns(AppPath.ADMIN_CHAPTER + "/*")

			// 黒本問題
			.addPathPatterns(AppPath.ADMIN_KUROHON_QUESTIONS_INSERT)
			.addPathPatterns(AppPath.ADMIN_KUROHON_QUESTIONS + "/*");
	}
}
