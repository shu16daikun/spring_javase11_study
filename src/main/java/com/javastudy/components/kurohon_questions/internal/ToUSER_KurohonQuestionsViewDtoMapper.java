package com.javastudy.components.kurohon_questions.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.my.util.type.MyType;

@Component
public class ToUSER_KurohonQuestionsViewDtoMapper {

	USER_KurohonQuestionsViewDto fromEntity(
		final KurohonQuestionsEntity entity,
		final USER_SankouBooksViewDto bookViewDto,
		final USER_ChapterViewDto chapterViewDto) {

		final String viewId = KurohonQuestionsIdBridge.toViewId(nonNullOrEmpty(entity.getId()));
		final String no = nonNullOrEmpty(entity.getQuestionNo());
		final int answers = Math.max(0, entity.getAnswerCountMax());
		final int options = Math.max(0, entity.getOptionCount());
		final String correctOption = nonNullOrEmpty(entity.getCorrectOption());
		final String explanationHtml = nonNullOrEmpty(entity.getExplanationHtml());

		return new USER_KurohonQuestionsViewDto(
			viewId,
			no,
			answers,
			options,
			bookViewDto, // 呼び出し側で解決済み
			chapterViewDto, // 呼び出し側で解決済み
			correctOption,
			explanationHtml);
	}

	/* 機能：値整形 */
	private static String nonNullOrEmpty(final String s) {
		return MyType.isNull(s) ? MyType.EMPTY : s;
	}
}
