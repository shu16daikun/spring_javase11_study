/*
 * ToADMIN_KurohonQuestionsViewDtoMapper.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.internal
 * Author  : shu-kundeath
 * Created : 2025/10/25 16:48:20
 *
 * 目的:
 * - KurohonQuestionsEntity → ADMIN_KurohonQuestionsViewDto の純粋変換（DIなし）
 *
 * 注意:
 * - 外部解決が必要な値（書誌DTO／章DTO／isUse）は呼び出し側で解決し、引数で渡す。
 */

package com.javastudy.components.kurohon_questions.internal;

import org.springframework.stereotype.Component;

/* ===== [import] START ===== */
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
/* ===== [import] END ===== */

@Component
public class ToADMIN_KurohonQuestionsViewDtoMapper {

	public ADMIN_KurohonQuestionsViewDto fromEntity(
		final KurohonQuestionsEntity entity,
		final ADMIN_SankouBooksViewDto booksViewDto,
		final ADMIN_ChapterViewDto chapterViewDto,
		final boolean isUse) {

		final String viewId = KurohonQuestionsIdBridge.toViewId(entity.getId());
		return new ADMIN_KurohonQuestionsViewDto(
			entity.getId(),
			viewId,
			booksViewDto,
			chapterViewDto,
			entity.getQuestionNo(),
			entity.getQuestionHtml(),
			entity.getCorrectOption(),
			entity.getExplanationHtml(),
			entity.getAnswerCountMax(),
			entity.getOptionCount(),
			isUse);
	}
}
