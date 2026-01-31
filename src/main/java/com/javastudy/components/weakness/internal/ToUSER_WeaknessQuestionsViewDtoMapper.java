package com.javastudy.components.weakness.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessQuestionsViewDto;
import com.util.type.MyType;

@Component
public class ToUSER_WeaknessQuestionsViewDtoMapper {

	public USER_WeaknessQuestionsViewDto fromEntity(
		final WeaknessEntity e,
		final USER_ChapterViewDto chapterViewDto,
		final USER_KurohonQuestionsViewDto questionsViewDto) {
		return new USER_WeaknessQuestionsViewDto(
			chapterViewDto,
			questionsViewDto,
			MyType.round1(e.getCorrectRate()),
			e.getTotalAttempts(),
			e.getLastIsCorrect(),
			e.getLastAnsweredAt());
	}
}
