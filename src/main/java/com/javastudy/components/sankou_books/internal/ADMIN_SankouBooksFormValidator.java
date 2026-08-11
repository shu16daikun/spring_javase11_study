/*
 * ADMIN_SankouBooksFormValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - Bean Validation で拾い切れない補助チェック（トリム／外部キー viewId の空チェック）
 *
 * 注意:
 * - 文字列は private static final String
 */

package com.javastudy.components.sankou_books.internal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.my.util.type.MyType;

@Component
public class ADMIN_SankouBooksFormValidator implements Validator {

	private static final String F_COLOR = "colorViewId";
	private static final String L_COLOR = "参考書カラー";

	@Override
	public boolean supports(final Class<?> clazz) {
		return ADMIN_SankouBooksForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (!(target instanceof final ADMIN_SankouBooksForm form)) {
			return;
		}
		// name をトリム（BeanValidationは NotBlank/Size を担当）
		if (form.getName() != null) {
			final String t = form.getName().trim();
			if (!t.equals(form.getName()))
				form.setName(t);
		}
		// colorViewId をトリム＋空チェック
		if (form.getColorViewId() != null) {
			final String t = form.getColorViewId().trim();
			if (!t.equals(form.getColorViewId()))
				form.setColorViewId(t);
		}
		if (MyType.isBlank(form.getColorViewId())) {
			errors.rejectValue(F_COLOR, ErrorProp.COMMON_NOT_BLANK, new Object[] {
				L_COLOR
			}, null);
		}
	}
}
