// com.javastudy.components.users.internal.USER_UsersFormValidator
package com.javastudy.components.users.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;

/**
 * 【機能】ユーザー更新フォーム Validator
 *
 * <p>
 * 目的：BeanValidation で足りない画面向けメッセージ整形等を補完。
 *
 * <h2>画面に見せるもの／ログ専用</h2>
 *
 * <ul>
 * <li>画面：項目ラベル付きの人間可読メッセージのみ。
 * <li>ログ：内部理由は最小限に抑制。
 * </ul>
 */
@Component
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_UsersFormValidator implements Validator {

	/* ===== [private] START ===== */
	private final ValidationMessageUtil msg;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	@Override
	public boolean supports(final Class<?> clazz) {
		return USER_UsersForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		final USER_UsersForm form = (USER_UsersForm) target;

		if (isBlank(form.getUsername())) {
			// ラベル付きでメッセージ解決（Form名.項目名 が labelKey として参照）
			errors.rejectValue(
				UsersFormParam.USERNAME,
				ErrorProp.COMMON_NOT_BLANK,
				msg.getMessageWithLabel(
					UsersFormParam.FORM, UsersFormParam.USERNAME,
					ErrorProp.COMMON_NOT_BLANK));
		}
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** 判定：空白。 */
	private static boolean isBlank(final String s) {
		return MyType.isBlank(s);
	}
	/* ===== [private] END ===== */
}
