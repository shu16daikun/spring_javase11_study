// com.javastudy.components.error.api.advice.GlobalUnexpectedExceptionAdvice.java
package com.javastudy.components.error.api.advice;

import static org.slf4j.MDC.*;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.javastudy.components.error.api.dto.ErrorViewDto;
import com.javastudy.components.error.api.service.ErrorStateService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.my.util.security.role.RoleUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 想定外の例外を UI 向け情報へ変換するグローバルハンドラ。
 * - MyRuntimeException 以外（業務外）を対象
 * - フロントからの errorCode/status も取り込み、同一エラー画面に誘導
 */
@Slf4j
@Component
@ControllerAdvice
@RequiredArgsConstructor
@Order(10) // 業務例外(@Order(5))より後段
public class GlobalUnexpectedExceptionAdvice {

	/* ===== [constants] START ===== */
	private static final String LOG_FMT = "EX_UNEXPECTED status={} traceId={} viewId={}";
	private static final String DEFAULT_ERROR_CODE = "UNEXPECTED";
	private static final String COOKIE_PARAM = "JSESSIONID";

	// フロントから受け取る入口（ヘッダ／クエリ）
	private static final String HDR_ERROR_CODE = "X-App-Error-Code";
	private static final String HDR_STATUS = "X-App-Error-Status";
	// クエリ名は既存 PropKey と合わせる
	private static final String Q_ERROR_CODE = PropKey.ERROR_CODE;
	private static final String Q_STATUS = PropKey.STATUS;
	/* ===== [constants] END ===== */

	private final ValidationMessageUtil msg;
	private final ErrorStateService errorStateService;

	@ExceptionHandler(Exception.class)
	public String handle(
		final Exception ex,
		final RedirectAttributes redirect,
		final HttpServletRequest req,
		final HttpServletResponse res) throws NoResourceFoundException {

		/* --- 静的 404 (.well-known) は対象外（ブラウザの自動プローブ等） --- */
		if (ex instanceof NoResourceFoundException) {
			final String uri = req.getRequestURI();
			if (uri != null && uri.startsWith("/.well-known")) {
				throw (NoResourceFoundException) ex;
			}
		}

		/* --- 例外→HTTPステータス（500 決め打ち廃止） --- */
		final HttpStatusCode resolved = this.resolveHttpStatus(ex);

		/* --- フロントからの上書き（任意） --- */
		final String errorCode = this.findFrontErrorCode(req);
		final int status = this.findFrontStatus(req).orElse(resolved.value());

		final String traceId = UUID.randomUUID().toString();
		final String message = this.resolveMessage(errorCode); // error.<code> 無ければ共通文言

		log.error(LOG_FMT, status, traceId, get("viewId"), ex);

		// Flash + URL に traceId を載せる（PRG）
		redirect.addFlashAttribute(PropKey.STATUS, status);
		redirect.addFlashAttribute(PropKey.ERROR_CODE, errorCode);
		redirect.addFlashAttribute(PropKey.ERROR_MESSAGE, message);
		redirect.addFlashAttribute(PropKey.TRACE_ID, traceId);
		redirect.addAttribute(PropKey.TRACE_ID, traceId);

		// 一時保存（再読込でも出せるように）
		this.errorStateService.put(
			traceId,
			new ErrorViewDto(status, errorCode, message, System.currentTimeMillis()));

		final String uri = req.getRequestURI() != null ? req.getRequestURI() : "";
		if (uri.startsWith(AppPath.LOGIN)) {
			this.hardLogout(req, res);
			return AppPath.R_LOGIN_ERROR;
		}
		return this.decideUserOrAdmin();
	}

	/* ===== [private] START ===== */

	/** 例外から HTTP ステータスを推定（なければ 500） */
	private HttpStatusCode resolveHttpStatus(final Exception ex) {
		if (ex instanceof final ResponseStatusException rse)
			return rse.getStatusCode();

		final ResponseStatus ann = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(),
			ResponseStatus.class);
		if (ann != null) {
			final HttpStatus code = ann.code() != HttpStatus.INTERNAL_SERVER_ERROR ? ann.code()
				: ann.value();
			if (code != null)
				return code;
		}
		if (ex instanceof AccessDeniedException)
			return HttpStatus.FORBIDDEN;
		if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException
			|| ex instanceof BindException
			|| ex instanceof ConstraintViolationException
			|| ex instanceof MethodArgumentTypeMismatchException
			|| ex instanceof MissingServletRequestParameterException) {
			return HttpStatus.BAD_REQUEST;
		}
		if (ex instanceof HttpRequestMethodNotSupportedException)
			return HttpStatus.METHOD_NOT_ALLOWED;
		if (ex instanceof HttpMediaTypeNotSupportedException)
			return HttpStatus.UNSUPPORTED_MEDIA_TYPE;

		// （必要なら追加）
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	/** フロントからの errorCode（ヘッダ優先→クエリ）。無ければ既定 "UNEXPECTED" */
	private String findFrontErrorCode(final HttpServletRequest req) {
		final String raw = orElseBlank(req.getHeader(HDR_ERROR_CODE),
			orElseBlank(req.getParameter(Q_ERROR_CODE), null));
		if (raw == null)
			return DEFAULT_ERROR_CODE;
		final String upper = raw.trim().toUpperCase();
		return upper.matches("[A-Z0-9_]{1,64}") ? upper : DEFAULT_ERROR_CODE;
	}

	/** フロントからの status（ヘッダ優先→クエリ）。無ければ空 */
	private Optional<Integer> findFrontStatus(final HttpServletRequest req) {
		final String s = orElseBlank(req.getHeader(HDR_STATUS), req.getParameter(Q_STATUS));
		if (s == null)
			return Optional.empty();
		try {
			final int v = Integer.parseInt(s);
			return (v >= 400 && v < 600) ? Optional.of(v) : Optional.empty();
		} catch (final NumberFormatException ignore) {
			return Optional.empty();
		}
	}

	/** error.<CODE> があればそれを、無ければ共通文言を返す */
	private String resolveMessage(final String errorCode) {
		try {
			return this.msg.getMessage("error." + errorCode);
		} catch (final Exception ignore) {
			return this.msg.getMessage(PropKey.ErrorProp.COMMON_UNEXPECTED);
		}
	}

	private String decideUserOrAdmin() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getAuthorities() != null) {
			final boolean isAdmin = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(a -> RoleUtil.ROLE_ADMIN.equals(a));
			if (isAdmin)
				return AppPath.R_ADMIN_ERROR;
		}
		return AppPath.R_USER_ERROR;
	}

	private void hardLogout(final HttpServletRequest req, final HttpServletResponse res) {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			new SecurityContextLogoutHandler().logout(req, res, auth);
		} catch (final Exception ignore) {
		}
		try {
			final var s = req.getSession(false);
			if (s != null)
				s.invalidate();
		} catch (final IllegalStateException ignore) {
		}
		try {
			new HttpSessionRequestCache().removeRequest(req, res);
		} catch (final Exception ignore) {
		}
		try {
			final Cookie cookie = new Cookie(COOKIE_PARAM, "");
			cookie.setPath("/");
			cookie.setMaxAge(0);
			cookie.setHttpOnly(true);
			res.addCookie(cookie);
		} catch (final Exception ignore) {
		}
		MDC.clear();
		SecurityContextHolder.clearContext();
	}

	private static String orElseBlank(final String a, final String b) {
		if (a != null && !a.isBlank())
			return a;
		if (b != null && !b.isBlank())
			return b;
		return null;
	}
	/* ===== [private] END ===== */
}
