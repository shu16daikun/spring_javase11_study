package com.javastudy.util.param.browser_guard;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.util.security.browser_guard.BrowserGuard;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@ControllerAdvice
@RequiredArgsConstructor
@Order(2)
public class GlobalBrowserGuardAdvice {

	private final ValidationMessageUtil msg;

	@ModelAttribute
	public void addBrowserGuard(final Model model, final HttpServletRequest request) {
		final Object raw = model.getAttribute(BrowserGuard.PARAM);
		final String requestUri = request.getRequestURI();

		// ★特例：
		//   アプリ起動直後の「初回 /login アクセス」は
		//   BrowserGuard 未設定(raw == null)でもエラーにしない。
		final boolean isInitialLoginAccess = AppPath.LOGIN.equals(requestUri) && (raw == null);

		// 1) コードを決定して Model に載せる
		final String code;
		if (raw == null) {
			// コントローラ側で何も積まれていない場合 → NONE をデフォルトで積む
			code = BrowserGuard.none();
			model.addAttribute(BrowserGuard.PARAM, code);
		} else {
			code = BrowserGuard.resolveCode(raw);
			model.addAttribute(BrowserGuard.PARAM, code);
		}

		// 2) 初回 /login アクセスだけは、NONE でもエラーメッセージを出さずに終了
		if (isInitialLoginAccess) {
			return;
		}

		// 3) それ以外は「OK 以外（NONE 含む）は異常」とみなして共通エラーを付与
		if (!BrowserGuard.ok().equals(code)) {
			model.addAttribute(
				PropKey.ERROR_MESSAGE,
				this.msg.getMessage(ErrorProp.COMMON_BAD_REQUEST));
		}
	}
}
