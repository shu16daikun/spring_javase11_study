/*
 * ADMIN_SankouBookColorFormValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_book_color.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:19:49
 *
 * 目的:
 * - Bean Validationで拾い切れない補助チェック（トリム／正規表現ポリシー）
 *
 * 注意:
 * - 文字列は private static final String
 */

package com.javastudy.components.sankou_book_color.internal;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.my.util.type.MyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/* ===== [import] END ===== */

/**
 * ADMIN_SankouBookColorFormValidator 目的: name の前後空白除去と、properties 由来の正規表現での検証
 */
@Component
@RequiredArgsConstructor
public class ADMIN_SankouBookColorFormValidator implements Validator {

	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	private final ValidationMessageUtil msg;

	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	@Override
	public boolean supports(final Class<?> clazz) {
		return ADMIN_SankouBookColorForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (!(target instanceof ADMIN_SankouBookColorForm form)) {
			return;
		}
		// 正規化（前後空白）
		if (MyType.isNotNull(form.getName())) {
			final String t = form.getName().trim();
			if (!t.equals(form.getName())) {
				form.setName(t);
			}
		}
		// 空は @NotBlank に委譲
		if (MyType.isBlank(form.getName())) {
			return;
		}
		// 正規表現（PropKey.RegexProp を経由）
		final String regex = msg.getMessage(RegexProp.BOOK_COLOR_NAME);
		if (!form.getName().matches(regex)) {
			errors.rejectValue(
				SankouBookColorFormParam.NAME, ErrorProp.SBC_NAME_PATTERN, new Object[] {
					"カラー名"
				},
				null);
		}
	}
	/* ===== [public/protected] END ===== */
}
