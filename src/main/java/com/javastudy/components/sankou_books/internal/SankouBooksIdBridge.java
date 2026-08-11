// com.javastudy.components.sankou_books.internal.SankouBooksIdBridge
package com.javastudy.components.sankou_books.internal;

import com.my.util.security.id.IdBridge;

/**
 * 【機能】SankouBooks IDブリッジ（EntityId ↔ ViewId）
 *
 * <p>
 * 目的：内部ID（EntityId）とUI契約ID（ViewId）を変換・照合（改ざん検知）。
 *
 * <h2>契約</h2>
 *
 * <ul>
 * <li>接頭辞／桁数は {@link SankouBooksIdConstants} に従う。
 * <li>ViewId 形式は {@code p<digits>-<sig>}（署名付き）。 <|diff_marker|> ADD A1080
 * </ul>
 *
 * <h2>設計メモ</h2>
 *
 * <ul>
 * <li>詳細なチェック/署名方式は {@link IdBridge} に委譲。
 * <li>⚠ 採番（DB側）の接頭辞・桁設定と <code>SankouBooksIdConstants</code> の整合を保つこと。
 * </ul>
 */
final class SankouBooksIdBridge {

	/* ===== [private] START ===== */
	private static final String PREFIX = SankouBooksIdConstants.getPrefix();
	private static final int TOTAL = SankouBooksIdConstants.getTotalLength();

	private SankouBooksIdBridge() {
	}

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	/** 機能：EntityID→ViewID 変換。 */
	static final String toViewId(final String entityId) {
		return IdBridge.toViewId(entityId);
	}

	/** 機能：ViewID→EntityID候補 変換。 */
	static final String toEntityId(final String viewId) {
		return IdBridge.toEntityId(viewId, PREFIX, TOTAL);
	}

	/** 機能：照合（改ざん検知）。 */
	static final void assertMatches(final String viewId, final String entityId) {
		IdBridge.assertMatches(viewId, entityId);
	}

	/* ===== [public/protected] END ===== */
}
