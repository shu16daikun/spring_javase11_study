package com.javastudy.components.answer_records.internal;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.my.util.type.MyType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/* 機能：解答フォームのサーバサイド検証 */
@Component
@AllArgsConstructor
public class USER_AnswerRecordsFormValidator implements Validator {

	private final ValidationMessageUtil msg;

	@Override
	public boolean supports(final Class<?> clazz) {
		return USER_AnswerRecordsForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		final USER_AnswerRecordsForm form = (USER_AnswerRecordsForm) target;

		if (isEmptySelected(form)) {
			errors.rejectValue(
				AnswerRecordsFormParam.OPTION,
				ErrorProp.COMMON_NOT_BLANK,
				msg.getMessage(AnswerRecordsFormParam.OPTION, ErrorProp.COMMON_NOT_BLANK));
		}
		if (isUnknownAndOtherSelected(form)) {
			errors.rejectValue(
				AnswerRecordsFormParam.OPTION,
				ErrorProp.ANSWER_OPT_UNKNOWN,
				msg.getMessage(AnswerRecordsFormParam.OPTION, ErrorProp.ANSWER_OPT_UNKNOWN));
		}
		if (isOverAnswerCountMax(form)) {
			errors.rejectValue(
				AnswerRecordsFormParam.OPTION,
				ErrorProp.ANSWER_OPT_MAX,
				msg.getMessage(AnswerRecordsFormParam.OPTION, ErrorProp.ANSWER_OPT_MAX));
		}
	}

	/* ==== 判定ユーティリティ ==== */
	private boolean isEmptySelected(final USER_AnswerRecordsForm form) {
		final List<String> opts = getOptionsSafe(form);
		return MyType.isEmpty(opts);
	}

	private boolean isUnknownAndOtherSelected(final USER_AnswerRecordsForm form) {
		final List<String> opts = getOptionsSafe(form);
		return hasUnknown(opts) && isMultiple(opts);
	}

	private boolean isOverAnswerCountMax(final USER_AnswerRecordsForm form) {
		final List<String> opts = getOptionsSafe(form);
		final int max = getAnswerCountMaxSafe(form);
		return MyType.isGreater(opts.size(), max);
	}

	private List<String> getOptionsSafe(final USER_AnswerRecordsForm form) {
		final List<String> src = form.getSelectedOption();
		return MyType.isNull(src) ? new ArrayList<>() : src;
	}

	private int getAnswerCountMaxSafe(final USER_AnswerRecordsForm form) {
		if (form.getAnswerCountMax() > 0) {
			return form.getAnswerCountMax();
		}
		if (MyType.isNotNull(form.getKurohonQuestions())) {
			return form.getKurohonQuestions().answerCountMax();
		}
		return Integer.MAX_VALUE;
	}

	private boolean hasUnknown(final List<String> list) {
		for (final String v : list) {
			if ("UNKNOWN".equals(v)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMultiple(final List<?> list) {
		return MyType.isGreater(list.size(), 1);
	}
}
