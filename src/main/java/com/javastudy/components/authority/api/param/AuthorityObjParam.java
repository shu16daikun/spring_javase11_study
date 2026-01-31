/*
 * ADMIN_AuthorityObjParam.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.api.param
 * Author  : shu-kundeath
 * Created : 2025/10/24 17:10:46
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.authority.api.param;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * ADMIN_AuthorityObjParam 目的: TODO 責務: - TODO
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthorityObjParam {
	/* ===== [public/protected] START ===== */
	public static final String VIEW_DTO = "authorityViewDto";
	public static final String VIEW_DTO_LIST = "authorityViewDtoList";
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/* ===== [private] END ===== */
}
