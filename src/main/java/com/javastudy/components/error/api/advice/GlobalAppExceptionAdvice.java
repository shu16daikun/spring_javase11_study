/*
 * GlobalAppExceptionAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.config.logging.advice
 * Author  : shu-kundeath
 * Created : 2025/10/23 17:20:00
 *
 * 目的:
 * - 業務例外(MyRuntimeException)を集約し、画面表示に必要な情報(status/code/message/traceId)へ変換
 * - 画面遷移先を /login 配下 or 権限(ADMIN/USER)で振り分け
 *
 * 注意:
 * - 文字列定数は private static final String を用いる
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.error.api.advice;

/* ===== [import] START ===== */
import static org.slf4j.MDC.*;

import com.exception.contents.MyRuntimeException;
import com.javastudy.components.error.api.dto.ErrorViewDto;
import com.javastudy.components.error.api.service.ErrorStateService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.util.type.MyType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/* ===== [import] END ===== */

/**
 * GlobalAppExceptionAdvice
 *
 * <p>
 * 目的: - 業務例外(MyRuntimeException)の一括ハンドリング - /login 配下は R_LOGIN_ERROR、それ以外は権限で
 * R_ADMIN_ERROR /
 * R_USER_ERROR へ遷移
 *
 * <p>
 * 責務: - status/code/message/traceId を決定して Flash/Query へ設定（PRG） - traceId をキーに
 * ErrorStateService
 * へ短期保存（エラー画面で復元） - ログは EX_HANDLED として集約（viewId は MDC より付与）
 *
 * <p>
 * 公開契約: - 画面に出すのは UI 向け情報のみ（内部構造はログ専用）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Slf4j
@Component
@ControllerAdvice
@RequiredArgsConstructor
@Order(5)
public class GlobalAppExceptionAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private static final String LOG_FMT = "EX_HANDLED code={} status={} traceId={} viewId={}";
	private final ValidationMessageUtil msg;
	private final ErrorStateService errorStateService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/**
	 * 業務例外を集約し、適切なエラー画面へリダイレクトする。
	 *
	 * @param ex
	 *            MyRuntimeException
	 * @param redirect
	 *            PRG用の Flash/Query
	 * @param req
	 *            HttpServletRequest
	 * @param res
	 *            HttpServletResponse
	 * @return AppPath.R_LOGIN_ERROR / R_ADMIN_ERROR / R_USER_ERROR
	 */
	@ExceptionHandler(MyRuntimeException.class)
	public String handle(
		final MyRuntimeException ex,
		final RedirectAttributes redirect,
		final HttpServletRequest req,
		final HttpServletResponse res) {

		final String traceId = UUID.randomUUID().toString();
		final int status = ex.getErrorCode().getHttpStatus().value();
		final String code = ex.getErrorCode().getCode();
		final String message = this.msg.getMessage(ex.getErrorCode().getMessageKey());

		log.error(LOG_FMT, code, status, traceId, get("viewId"), ex);

		redirect.addFlashAttribute(PropKey.STATUS, status);
		redirect.addFlashAttribute(PropKey.ERROR_CODE, code);
		redirect.addFlashAttribute(PropKey.ERROR_MESSAGE, message);
		redirect.addFlashAttribute(PropKey.TRACE_ID, traceId);
		redirect.addAttribute(PropKey.TRACE_ID, traceId);

		this.errorStateService.put(
			traceId, new ErrorViewDto(status, code, message, System.currentTimeMillis()));

		final String uri = MyType.isNotBlank(req.getRequestURI()) ? req.getRequestURI() : "";
		if (uri.startsWith(AppPath.LOGIN)) {
			this.hardLogout(req, res);
			return AppPath.R_LOGIN_ERROR;
		}
		return this.decideUserOrAdmin();
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	private String decideUserOrAdmin() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getAuthorities() != null) {
			final boolean isAdmin = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(a -> "ROLE_ADMIN".equals(a));
			if (isAdmin) {
				return AppPath.R_ADMIN_ERROR;
			}
		}
		return AppPath.R_USER_ERROR;
	}

	private void hardLogout(final HttpServletRequest req, final HttpServletResponse res) {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// 標準の logout (session invalidate / security context clear をやる)
		try {
			new SecurityContextLogoutHandler().logout(req, res, auth);
		} catch (final Exception e) {
			log.warn("logout handler failed", e);
		}

		// 念のためセッション破棄（logout が無効化出来てないケースに備える）
		try {
			final var session = req.getSession(false);
			if (session != null) {
				session.invalidate();
			}
		} catch (final IllegalStateException ignore) {
		}

		// SavedRequest（リダイレクト復元情報）を除去
		try {
			new HttpSessionRequestCache().removeRequest(req, res);
		} catch (final Exception ignore) {
		}

		// JSESSIONID を確実に消す（ブラウザ側）
		try {
			final Cookie cookie = new Cookie("JSESSIONID", "");
			cookie.setPath("/");
			cookie.setMaxAge(0);
			cookie.setHttpOnly(true);
			res.addCookie(cookie);
		} catch (final Exception ignore) {
		}

		// ログ用コンテキストやスレッドローカルをクリア
		MDC.clear();
		SecurityContextHolder.clearContext();

		// （任意）アプリで Spring Cache を使っていて消したければ、CacheManager を注入して clear する
		// ただしこれは依存追加なので、嫌ならスルーでOK。
	}

	/* ===== [private] END ===== */
}
