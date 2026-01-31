/*
 * ToADMIN_ChapterViewDtoMapper.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.chapter.internal
 * Author  : shu-kundeath
 * Created : 2025/10/25 14:08:51
 *
 * 目的:
 * - ChapterEntity → ADMIN_ChapterViewDto への純粋変換（DIなし）
 *
 * 注意:
 * - 依存注入しない。外部解決が必要な値は呼び出し側で解決し引数で渡す。
 */

package com.javastudy.components.chapter.internal;

import org.springframework.stereotype.Component;

/* ===== [import] START ===== */
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
/* ===== [import] END ===== */

@Component
public class ToADMIN_ChapterViewDtoMapper {

	public ADMIN_ChapterViewDto fromEntity(
		final ChapterEntity entity,
		final ADMIN_SankouBooksViewDto booksViewDto,
		final boolean isUse) {

		final String viewId = ChapterIdBridge.toViewId(entity.getId());
		return new ADMIN_ChapterViewDto(
			entity.getId(),
			viewId,
			entity.getNo(),
			entity.getName(),
			booksViewDto,
			isUse);
	}
}
