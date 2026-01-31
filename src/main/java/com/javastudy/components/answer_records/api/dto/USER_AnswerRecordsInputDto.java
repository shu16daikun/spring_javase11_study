package com.javastudy.components.answer_records.api.dto;

import com.util.type.MyType;

import jakarta.validation.constraints.NotBlank;

/* 機能：解答記録 入力DTO（擬似IDで受け取り、ServiceでEntityIDへブリッジ） */
public record USER_AnswerRecordsInputDto(
	@NotBlank String questionViewId,

	/*  */
	@NotBlank String sankouBookViewId,

	/* */
	@NotBlank String chapterViewId,

	/* 選択結果（"A"/"AB"/"UNKNOWN"等）。空文字可 */
	String selectedOption,

	/* 正答 */
	@NotBlank String correctOption) {
	public String selectedOptionOrEmpty() {
		return MyType.orEmpty(selectedOption);
	}
}
