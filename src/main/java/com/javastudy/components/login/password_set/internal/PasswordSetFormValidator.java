// PasswordSetFormValidator.java
package com.javastudy.components.login.password_set.internal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.util.type.MyType;

import lombok.RequiredArgsConstructor;

/**
 * パスワード再設定フォームのサーバサイド検証。
 *
 * <p>
 * “画面に見せるもの”：BindingResult 経由でフィールド単位のエラーメッセージを返す。
 *
 * <p>
 * “ログ専用”：本クラスではログ出力しない（ControllerAdvice 側に集約）。
 */
@Component
@RequiredArgsConstructor
public class PasswordSetFormValidator implements Validator {

	/* ===== [private] START ===== */
	private final ValidationMessageUtil msg;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/** {@inheritDoc} */
	@Override
	public boolean supports(final Class<?> clazz) {
		return PasswordSetForm.class.isAssignableFrom(clazz);
	}

	/**
	 * 必須／一致チェックのみ実施。形式は BeanValidation（@ValidPassword）に委譲。
	 *
	 * @param target
	 *            対象フォーム
	 * @param errors
	 *            エラー格納先
	 */
	@Override
	public void validate(final Object target, final Errors errors) {
		final PasswordSetForm form = (PasswordSetForm) target;

		if (MyType.isBlank(form.getPassword())) {
			errors.rejectValue(
				PasswordSetFormParam.PASSWORD,
				ErrorProp.COMMON_NOT_BLANK,
				msg.getMessageWithLabel(
					PasswordSetFormParam.FORM,
					PasswordSetFormParam.PASSWORD,
					ErrorProp.COMMON_NOT_BLANK));
		}
		if (MyType.isBlank(form.getPasswordCheck())) {
			errors.rejectValue(
				PasswordSetFormParam.PASS_CHECK,
				ErrorProp.COMMON_NOT_BLANK,
				msg.getMessageWithLabel(
					PasswordSetFormParam.FORM,
					PasswordSetFormParam.PASS_CHECK,
					ErrorProp.COMMON_NOT_BLANK));
		}
		if (isPasswordMismatch(form)) {
			errors.rejectValue(
				PasswordSetFormParam.PASS_CHECK,
				ErrorProp.USER_PASSWORD_MISMATCH,
				msg.getMessage(ErrorProp.USER_PASSWORD_MISMATCH));
		}
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** 同値の否定（パスワード不一致）判定のみの小ユーティリティ。 */
	private static boolean isPasswordMismatch(final PasswordSetForm form) {
		return MyType.isNotEqual(form.getPassword(), form.getPasswordCheck());
	}
	/* ===== [private] END ===== */
}
