/*
 * ADMIN_AuthorityFormValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:35:34
 *
 * 目的:
 * - 画面入力の最終確認（トリムや将来の追加チェック用）
 *
 * 注意:
 * - 文字列定数は private static final String（規約）
 */

package com.javastudy.components.authority.internal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/* ===== [import] START ===== */
import com.my.util.type.MyType;

/* ===== [import] END ===== */

/**
 * ADMIN_AuthorityFormValidator 目的: Bean Validation で拾い切れない補助チェック（軽量）
 *
 * <p>
 * 公開契約: - supports は厳密に ADMIN_AuthorityForm を返す
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Component
public class ADMIN_AuthorityFormValidator implements Validator {

	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [public/protected] START ===== */
	@Override
	public boolean supports(final Class<?> clazz) {
		return ADMIN_AuthorityForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (!(target instanceof final ADMIN_AuthorityForm form)) {
			return;
		}

		// 軽い正規化（前後空白は UI/Controller で落とす）
		if (MyType.isNotNull(form.getSystemName())) {
			final String trimmed = form.getSystemName().trim();
			if (!trimmed.equals(form.getSystemName())) {
				form.setSystemName(trimmed); // ここで揃えておく
			}
		}

		// 将来: 重複/危険名リストなどの追加ロジックはここに集約
		// 例）if (authorityService.existsExact(form.getName())) { errors.rejectValue(F_NAME,
		// "error.duplicate", new Object[] { "権限名" }, null); }
	}
	/* ===== [public/protected] END ===== */
}
