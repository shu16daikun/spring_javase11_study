package com.javastudy.components.answer_records.internal;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

/* 機能：解答フォーム（複数）用バリデータ */
@Component
@RequiredArgsConstructor
public class USER_AnswerRecordsFormListValidator implements Validator {

	private final USER_AnswerRecordsFormValidator itemValidator;

	@Override
	public boolean supports(final Class<?> clazz) {
		return USER_AnswerRecordsFormList.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		final var list = (USER_AnswerRecordsFormList) target;
		final List<USER_AnswerRecordsForm> items = list.getFormItems();
		for (int i = 0; i < items.size(); i++) {
			errors.pushNestedPath("formItems[" + i + "]");
			try {
				ValidationUtils.invokeValidator(itemValidator, items.get(i), errors);
			} finally {
				errors.popNestedPath();
			}
		}
	}
}
