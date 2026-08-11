// LoginFormValidator.java
package com.javastudy.components.login.login.internal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.javastudy.components.login.login.api.param.LoginFormParam;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.my.util.type.MyType;

/**
 * ログインフォームのサーバサイド検証。
 *
 * <p>
 * “画面に見せるもの”：BindingResult 経由で field error を返す。
 *
 * <p>
 * “ログ専用”：本クラス内ではログ出力しない（ControllerAdvice 側に集約）。
 */
@Component
public class LoginFormValidator implements Validator {

	/* ===== [public/protected] START ===== */
	/** {@inheritDoc} */
	@Override
	public boolean supports(final Class<?> clazz) {
		return LoginForm.class.isAssignableFrom(clazz);
	}

	/**
	 * 必須項目（username/password）の空チェックのみ実施。
	 *
	 * <p>
	 * 形式チェックは BeanValidation（@ValidUsername/@ValidPassword）に委譲。
	 *
	 * @param target
	 *            対象フォーム
	 * @param errors
	 *            追加先エラーコンテナ
	 */
	@Override
	public void validate(final Object target, final Errors errors) {
		final LoginForm form = (LoginForm) target;

		if (MyType.isBlank(form.getUsername())) {
			// 画面向け：フィールドエラーとしてUIへ伝える
			errors.rejectValue(LoginFormParam.USERNAME, ErrorProp.COMMON_NOT_BLANK);
		}
		if (MyType.isBlank(form.getPassword())) {
			errors.rejectValue(LoginFormParam.PASSWORD, ErrorProp.COMMON_NOT_BLANK);
		}
	}
	/* ===== [public/protected] END ===== */
}
