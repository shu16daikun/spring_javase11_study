/*
 * LoginControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.login_module.login.internal
 * Author  : shu-kundeath
 * Created : 2025/10/23 17:20:07
 *
 * 目的:
 * - ログイン画面の Binder 設定／Validation 追加／状態メッセージの投入
 *
 * 注意:
 * - 文字列定数は private static final String を用いる（本クラスでは未使用）
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.login.login.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/* ===== [import] START ===== */
import com.javastudy.components.login.login.api.param.LoginFormParam;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.my.util.type.MyType;

import lombok.RequiredArgsConstructor;

/* ===== [import] END ===== */

/**
 * LoginControllerAdvice
 *
 * <p>
 * 目的: - ログインフォームの検証と UI 向けメッセージの注入
 *
 * <p>
 * 責務: - Binder に LoginFormValidator を登録 - Validation メッセージの LOGIN 系 Key を追記
 * <|diff_marker|> ADD
 * A1660 - クエリ(error/logout)に応じて画面用メッセージを設定
 *
 * <p>
 * 公開契約: - 例外ハンドリングは GlobalAppExceptionAdvice で集約
 */
@ControllerAdvice(assignableTypes = LoginController.class)
@RequiredArgsConstructor
@Order(10)
public class LoginControllerAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final LoginFormValidator validator;
	private final ValidationMessageUtil msg;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@InitBinder(LoginFormParam.FORM)
	public void initBinder(final WebDataBinder binder) {
		binder.addValidators(validator);
	}

	@ModelAttribute
	public void addValidationMessages(
		final Model model,
		@RequestParam(value = "error", required = false) final String error,
		@RequestParam(value = "logout", required = false) final String logout) {

		@SuppressWarnings("unchecked")
		final Map<String, String> base = (Map<String, String>) model
			.getAttribute(PropKey.VALIDATION_MESSAGES);
		final Map<String, String> vm = (base != null)
			? base
			: new LinkedHashMap<>(msg.setModelValidationMessages());
		vm.put(ErrorProp.LOGIN_MISMATCH, msg.getMessage(ErrorProp.LOGIN_MISMATCH));
		model.addAttribute(PropKey.VALIDATION_MESSAGES, vm);

		if (MyType.isNotBlank(error)) {
			model.addAttribute(PropKey.ERROR_MESSAGE, msg.getMessage(ErrorProp.LOGIN_MISMATCH));
		}
		if (MyType.isNotBlank(logout)) {
			model.addAttribute(PropKey.ERROR_MESSAGE,
				msg.getMessage(ErrorProp.LOGIN_LOGOUT_SUCCESS));
		}
	}
	/* ===== [public/protected] END ===== */
}
