package com.javastudy.components.kurohon_questions.api.dto;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;

/* 機能：黒本：問題ビューDTO（ユーザー） */
public record USER_KurohonQuestionsViewDto(
	String viewId,
	String questionNo,
	int answerCountMax,
	int optionCount,
	USER_SankouBooksViewDto sankouBooksViewDto,
	USER_ChapterViewDto chapterViewDto,
	String correctOption,
	String explanationHtml) {
}
