/*
 * AuthorityDB.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.internal
 * Author  : shu-kundeath
 * Created : 2025/10/25 19:49:41
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.authority.internal;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * AuthorityDB 目的: TODO
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
final class AuthorityDB {
	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	// AuthorityDB 内
	static final class AuthorityIdParam {
		static final String SEQUENCE = "public.au_id_seq";
		static final String PREFIX = "AU";
		static final int PAD = 4; // "AU" + 4桁 = 6文字

		private AuthorityIdParam() {
		}
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/* ===== [private] END ===== */
}
