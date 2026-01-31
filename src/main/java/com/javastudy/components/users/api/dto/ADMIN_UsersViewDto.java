/*
 * ADMIN_UsersViewDto.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.api.dto
 * Author  : shu-kundeath
 * Created : 2025/10/24 13:20:34
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.users.api.dto;

import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * ADMIN_UsersViewDto 目的: DBデータを管理者画面に出力する。
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
public record ADMIN_UsersViewDto(
	String id,
	String viewId,
	String username,
	ADMIN_AuthorityViewDto authorityViewDto,
	boolean isFirstLogin,
	boolean isUse) {
	/* ===== [factory/static] START ===== */
	/* ===== [factory/static] END ===== */

}
