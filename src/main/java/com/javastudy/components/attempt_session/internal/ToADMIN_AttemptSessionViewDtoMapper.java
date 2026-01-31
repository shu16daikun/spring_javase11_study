/*
 * ToADMIN_AttemptSessionViewDtoMapper.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.attempt_session.internal
 * Author  : shu-kundeath
 * Created : 2025/10/25 17:07:48
 *
 * 目的:
 * - AttemptSessionEntity → 管理者画面表示DTO（純粋変換：DI依存なし）
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（必要に応じて private final String）
 */

package com.javastudy.components.attempt_session.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.attempt_session.api.dto.ADMIN_AttemptSessionViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

@Component
public class ToADMIN_AttemptSessionViewDtoMapper {

	public ADMIN_AttemptSessionViewDto fromEntity(
		final AttemptSessionEntity entity,
		final ADMIN_UsersViewDto usersViewDto,
		final ADMIN_SankouBooksViewDto booksViewDto) {

		final String viewId = AttemptSessionIdBridge.toViewId(entity.getId());
		return new ADMIN_AttemptSessionViewDto(
			entity.getId(),
			viewId,
			usersViewDto,
			booksViewDto,
			entity.getStartedAt(),
			entity.getFinishedAt());
	}
}
