package com.javastudy.components.kurohon_questions.internal;

import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsInputDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.util.zero_padding.ZeroPadding;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ADMIN_KurohonQuestionsForm {

	/** 外部キー：参考書（バインドは viewId） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	private String sankouBooksViewId;

	/** 表示補助（任意） */
	private ADMIN_SankouBooksViewDto sankouBooksViewDto;

	/** 外部キー：章（バインドは viewId） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	private String chapterViewId;

	/** 表示補助（任意） */
	private ADMIN_ChapterViewDto chapterViewDto;

	/** 問題番号：1..999（保存時は3桁ゼロ埋め文字列へ） */
	@NotNull(message = ErrorProp.COMMON_NOT_BLANK)
	@Min(value = KurohonQuestionsFormParam.QUESTION_NO_MIN, message = ErrorProp.COMMON_MIN)
	@Max(value = KurohonQuestionsFormParam.QUESTION_NO_MAX, message = ErrorProp.COMMON_MAX)
	private Integer questionNo;

	/** 問題本文（HTML）：任意。DB DEFAULTで空文字。 */
	private String questionHtml;

	/** 正答の表記（例：A / A;C など）：1..20 */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(max = KurohonQuestionsFormParam.CORRECT_OPTION_MAX, message = ErrorProp.COMMON_MAX)
	private String correctOption;

	/** 解説（HTML）：任意。DB DEFAULTで空文字。 */
	private String explanationHtml;

	/** 最大回答数：1.. */
	@NotNull(message = ErrorProp.COMMON_NOT_BLANK)
	@Min(value = 1, message = ErrorProp.COMMON_MIN)
	private Integer answerCountMax;

	/** 選択肢数：0.. */
	@NotNull(message = ErrorProp.COMMON_NOT_BLANK)
	@Min(value = 0, message = ErrorProp.COMMON_MIN)
	private Integer optionCount;

	public ADMIN_KurohonQuestionsInputDto toCreateInputDto() {
		return new ADMIN_KurohonQuestionsInputDto(
			null,
			this.sankouBooksViewId,
			this.chapterViewId,
			this.pad3(this.questionNo),
			this.trim(this.questionHtml),
			this.trim(this.correctOption),
			this.trim(this.explanationHtml),
			this.answerCountMax,
			this.optionCount);
	}

	public ADMIN_KurohonQuestionsInputDto toUpdateInputDto(final String viewId) {
		return new ADMIN_KurohonQuestionsInputDto(
			viewId,
			this.sankouBooksViewId,
			this.chapterViewId,
			this.pad3(this.questionNo),
			this.trim(this.questionHtml),
			this.trim(this.correctOption),
			this.trim(this.explanationHtml),
			this.answerCountMax,
			this.optionCount);
	}

	public static ADMIN_KurohonQuestionsForm fromViewDto(final ADMIN_KurohonQuestionsViewDto v) {
		final Integer questionNo = ZeroPadding.parseLeftPaddedInt(v.questionNo());
		return ADMIN_KurohonQuestionsForm.builder()
			.sankouBooksViewId(
				v.sankouBooksViewDto() != null ? v.sankouBooksViewDto().viewId() : null)
			.sankouBooksViewDto(v.sankouBooksViewDto())
			.chapterViewId(v.chapterViewDto() != null ? v.chapterViewDto().viewId() : null)
			.chapterViewDto(v.chapterViewDto())
			.questionNo(questionNo)
			.questionHtml(v.questionHtml())
			.correctOption(v.correctOption())
			.explanationHtml(v.explanationHtml())
			.answerCountMax(v.answerCountMax())
			.optionCount(v.optionCount())
			.build();
	}

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	/** 3桁ゼロ埋め（1→"001"、11→"011"、123→"123"） */
	private String pad3(final Integer n) {
		if (n == null) {
			return null;
		}
		return ZeroPadding.leftPadDigits(String.valueOf(n), 3);
	}
}
