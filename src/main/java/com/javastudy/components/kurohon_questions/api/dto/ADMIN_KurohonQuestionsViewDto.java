/*
 * ADMIN_KurohonQuestionsViewDto.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.api.dto
 * Author  : shu-kundeath
 * Created : 2025/10/24 18:30:07
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.kurohon_questions.api.dto;

import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * ADMIN_KurohonQuestionsViewDto 目的: DBデータを管理者画面に出力する
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
public record ADMIN_KurohonQuestionsViewDto(
	String id,
	String viewId,
	ADMIN_SankouBooksViewDto sankouBooksViewDto,
	ADMIN_ChapterViewDto chapterViewDto,
	String questionNo,
	String questionHtml,
	String correctOption,
	String explanationHtml,
	int answerCountMax,
	int optionCount,
	boolean isUse) {
	/* ===== [factory/static] START ===== */
	/* ===== [factory/static] END ===== */

}
