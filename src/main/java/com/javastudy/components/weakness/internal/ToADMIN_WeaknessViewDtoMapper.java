package com.javastudy.components.weakness.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.weakness.api.dto.ADMIN_WeaknessViewDto;

@Component
public class ToADMIN_WeaknessViewDtoMapper {

	public ADMIN_WeaknessViewDto fromEntity(
		final WeaknessEntity entity,
		final ADMIN_UsersViewDto usersViewDto,
		final ADMIN_SankouBooksViewDto booksViewDto,
		final ADMIN_ChapterViewDto chapterViewDto,
		final ADMIN_KurohonQuestionsViewDto questionsViewDto) {
		final String viewId = WeaknessIdBridge.toViewId(entity.getId());
		return new ADMIN_WeaknessViewDto(
			entity.getId(),
			viewId,
			usersViewDto,
			booksViewDto,
			chapterViewDto,
			questionsViewDto,
			entity.getTotalAttempts(),
			entity.getCorrectCount(),
			entity.getWrongCount(),
			entity.getCorrectRate(),
			entity.getLastAnsweredAt(),
			entity.getLastSelectedOption(),
			entity.getLastIsCorrect());
	}
}
