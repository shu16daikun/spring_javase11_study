package com.javastudy.components.answer_records.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsViewDto;
import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.my.util.type.MyType;

/**
 * AnswerRecordsEntity → USER_AnswerRecordsViewDto 変換（純粋変換：DI依存なし）。
 *
 * <p>
 * “画面に見せるもの”のみを構築する。
 */
@Component
public class ToUSER_AnswerRecordsViewDtoMapper {

	/**
	 * 変換（解決済みの外部DTOを引数で受け取る）。
	 */
	USER_AnswerRecordsViewDto fromEntity(
		final AnswerRecordsEntity entity,
		final MyUsersViewDto usersViewDto,
		final USER_KurohonQuestionsViewDto questionsViewDto,
		final USER_ChapterViewDto chapterViewDto) {

		final String viewId = AnswerRecordsIdBridge.toViewId(entity.getId());

		return new USER_AnswerRecordsViewDto(
			viewId,
			usersViewDto,
			questionsViewDto,
			chapterViewDto,
			MyType.orEmpty(entity.getSelectedOption()),
			entity.isCorrect(),
			entity.getAttemptNo());
	}
}
