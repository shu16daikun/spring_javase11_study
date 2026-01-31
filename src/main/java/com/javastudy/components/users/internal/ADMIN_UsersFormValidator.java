/*
 * ADMIN_UsersFormValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:23:26
 *
 * 目的:
 * - Bean Validation で拾い切れない補助チェック（トリム、将来のクロス項目）
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */
package com.javastudy.components.users.internal;

/* ===== [import] START ===== */
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/* ===== [import] END ===== */

/* ===== [public/protected] START ===== */
@Component
public class ADMIN_UsersFormValidator implements Validator {

	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [public/protected] START ===== */
	@Override
	public boolean supports(final Class<?> clazz) {
		return ADMIN_UsersForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (!(target instanceof ADMIN_UsersForm form)) {
			return;
		}
		// 軽い正規化（画面由来の前後空白だけここで吸収）
		if (form.getUsername() != null) {
			final String t = form.getUsername().trim();
			if (!t.equals(form.getUsername())) {
				form.setUsername(t);
			}
		}

		// 将来: クロス項目チェックや予約語チェック等をここに追加
		// 例）if (isReserved(form.getUsername())) { errors.rejectValue(F_USERNAME,
		// "error.username.reserved"); }
	}
	/* ===== [public/protected] END ===== */
}
/* ===== [public/protected] END ===== */
