/*
 * ADMIN_AttemptSessionViewDto.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.attempt_session.api.dto
 * Author  : shu-kundeath
 * Created : 2025/10/24 18:37:03
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.attempt_session.api.dto;

/* ===== [import] START ===== */
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import java.time.LocalDateTime;

/* ===== [import] END ===== */

/**
 * ADMIN_AttemptSessionViewDto 目的: DBデータを管理者画面に出力する
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
public record ADMIN_AttemptSessionViewDto(
	String id,
	String viewId,
	ADMIN_UsersViewDto usersViewDto,
	ADMIN_SankouBooksViewDto sankouBooksViewDto,
	LocalDateTime startedAt,
	LocalDateTime finishedAt) {
	/* ===== [factory/static] START ===== */
	/* ===== [factory/static] END ===== */

}
