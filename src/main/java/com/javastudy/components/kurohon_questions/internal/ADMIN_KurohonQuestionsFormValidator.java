/*
 * ADMIN_KurohonQuestionsFormValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - Bean Validation で拾い切れない補助チェック（正答のトリム／外部キー viewId の空チェック）
 */

package com.javastudy.components.kurohon_questions.internal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.my.util.type.MyType;

@Component
public class ADMIN_KurohonQuestionsFormValidator implements Validator {

	private static final String F_BOOK = "sankouBooksViewId";
	private static final String F_CHAPTER = "chapterViewId";
	private static final String L_BOOK = "参考書";
	private static final String L_CHAPTER = "章";

	@Override
	public boolean supports(final Class<?> clazz) {
		return ADMIN_KurohonQuestionsForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (!(target instanceof final ADMIN_KurohonQuestionsForm form)) {
			return;
		}
		// 正答をトリム
		if (form.getCorrectOption() != null) {
			final String t = form.getCorrectOption().trim();
			if (!t.equals(form.getCorrectOption()))
				form.setCorrectOption(t);
		}
		// 外部キー viewId をトリム
		if (form.getSankouBooksViewId() != null) {
			final String t = form.getSankouBooksViewId().trim();
			if (!t.equals(form.getSankouBooksViewId()))
				form.setSankouBooksViewId(t);
		}
		if (form.getChapterViewId() != null) {
			final String t = form.getChapterViewId().trim();
			if (!t.equals(form.getChapterViewId()))
				form.setChapterViewId(t);
		}
		// 空チェック
		if (MyType.isBlank(form.getSankouBooksViewId())) {
			errors.rejectValue(F_BOOK, ErrorProp.COMMON_NOT_BLANK, new Object[] {
				L_BOOK
			}, null);
		}
		if (MyType.isBlank(form.getChapterViewId())) {
			errors.rejectValue(F_CHAPTER, ErrorProp.COMMON_NOT_BLANK, new Object[] {
				L_CHAPTER
			}, null);
		}
		// 正答の空は BeanValidation(NotBlank) が担当。ここではトリムのみ。
	}
}
