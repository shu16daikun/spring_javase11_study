package com.javastudy.components.answer_records.internal;

import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsInputDto;
import com.util.type.MyType;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 機能：解答登録フォーム（複数） */
@Getter
@Setter
@NoArgsConstructor
final class USER_AnswerRecordsFormList {

	@Valid
	private List<USER_AnswerRecordsForm> formItems = new ArrayList<>();

	USER_AnswerRecordsFormList(final List<USER_AnswerRecordsForm> formItems) {
		this.formItems = MyType.isNull(formItems) ? new ArrayList<>() : new ArrayList<>(formItems);
	}

	final void add(final USER_AnswerRecordsForm form) {
		if (MyType.isNotNull(form)) {
			this.formItems.add(form);
		}
	}

	final List<USER_AnswerRecordsInputDto> toInputDto() {
		return this.formItems.stream().map(USER_AnswerRecordsForm::toInputDto).toList();
	}
}
