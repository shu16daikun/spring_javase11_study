/*
 * ToADMIN_AnswerRecordsViewDtoMapper.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.answer_records.internal
 * Author  : shu-kundeath
 * Created : 2025/10/25 17:27:32
 *
 * 目的:
 * - AnswerRecordsEntity → 管理者画面表示DTO（純粋変換：DI依存なし）
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（必要に応じて private final String）
 */

package com.javastudy.components.answer_records.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.answer_records.api.dto.ADMIN_AnswerRecordsViewDto;
import com.javastudy.components.attempt_session.api.dto.ADMIN_AttemptSessionViewDto;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.my.util.type.MyType;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * 弱点…ではなく AnswerRecords の管理者DTO変換。
 *
 * 公開契約: 例外は userCode のみ外部に出す（内部構造は伏せる）
 * 備考 : DTO は record を用いる
 */
@Component
public class ToADMIN_AnswerRecordsViewDtoMapper {

	public ADMIN_AnswerRecordsViewDto fromEntity(final AnswerRecordsEntity entity) {
		throw new UnsupportedOperationException(
			"Use fromEntity(entity, usersView, booksView, chapterView, questionsView, sessionView).");
	}

	public ADMIN_AnswerRecordsViewDto fromEntity(
		final AnswerRecordsEntity entity,
		final ADMIN_UsersViewDto usersViewDto,
		final ADMIN_SankouBooksViewDto booksViewDto,
		final ADMIN_ChapterViewDto chapterViewDto,
		final ADMIN_KurohonQuestionsViewDto questionsViewDto,
		final ADMIN_AttemptSessionViewDto sessionViewDto) {

		if (MyType.isNull(entity) || MyType.isNull(usersViewDto) || MyType.isNull(booksViewDto)
			|| MyType.isNull(chapterViewDto) || MyType.isNull(questionsViewDto)
			|| MyType.isNull(sessionViewDto)) {
			throw new IllegalArgumentException("null argument.");
		}

		final String viewId = AnswerRecordsIdBridge.toViewId(entity.getId());

		return new ADMIN_AnswerRecordsViewDto(
			entity.getId(),
			viewId,
			usersViewDto,
			booksViewDto,
			chapterViewDto,
			questionsViewDto,
			sessionViewDto,
			entity.getSelectedOption(),
			entity.isCorrect(),
			entity.getAnsweredAt(),
			entity.getAttemptNo());
	}
}
