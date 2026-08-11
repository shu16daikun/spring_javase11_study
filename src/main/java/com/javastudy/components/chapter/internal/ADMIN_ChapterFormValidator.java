/*
 * ADMIN_ChapterFormValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.chapter.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - Bean Validation で拾い切れない補助チェック（トリム／外部キー viewId の空チェック）
 *
 * 注意:
 * - 定数/文字列は private static final String
 */

package com.javastudy.components.chapter.internal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.my.util.type.MyType;

@Component
public class ADMIN_ChapterFormValidator implements Validator {

	private static final String F_BOOK = "sankouBooksViewId";
	private static final String L_BOOK = "参考書";

	@Override
	public boolean supports(final Class<?> clazz) {
		return ADMIN_ChapterForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (!(target instanceof final ADMIN_ChapterForm form)) {
			return;
		}
		// タイトルをトリム
		if (form.getTitle() != null) {
			final String t = form.getTitle().trim();
			if (!t.equals(form.getTitle()))
				form.setTitle(t);
		}
		// 参考書 viewId をトリム＋空チェック
		if (form.getSankouBooksViewId() != null) {
			final String t = form.getSankouBooksViewId().trim();
			if (!t.equals(form.getSankouBooksViewId()))
				form.setSankouBooksViewId(t);
		}
		if (MyType.isBlank(form.getSankouBooksViewId())) {
			errors.rejectValue(F_BOOK, ErrorProp.COMMON_NOT_BLANK, new Object[] {
				L_BOOK
			}, null);
		}
	}
}
