package com.javastudy.components.answer_records.api.dto;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;

/* 機能：解答記録 表示DTO（ユーザー） */
public record USER_AnswerRecordsViewDto(
	String viewId,
	MyUsersViewDto usersViewDto,
	USER_KurohonQuestionsViewDto questionsViewDto,
	USER_ChapterViewDto chapterViewDto,
	String selectedOption,
	boolean corrected,
	int attemptNo) {
}
