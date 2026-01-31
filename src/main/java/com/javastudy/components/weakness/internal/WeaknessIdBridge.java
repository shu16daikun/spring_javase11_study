// com.javastudy.components.weakness.internal.WeaknessIdBridge
package com.javastudy.components.weakness.internal;

import com.util.security.id.IdBridge;

/**
 * Weaknessの EntityId ↔ ViewId 変換ブリッジ。
 *
 * <p>
 * ViewIdは改ざん検知付き（{@code p<digits>-<sig>}）。
 */
final class WeaknessIdBridge {

	/* ===== [private] START ===== */
	private static final String PREFIX = WeaknessIdConstants.getPrefix();
	private static final int TOTAL = WeaknessIdConstants.getTotalLength();

	private WeaknessIdBridge() {
	}

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/**
	 * EntityId → ViewId へ変換。
	 *
	 * @param entityId
	 *            実ID（例: WE00001234）
	 * @return ViewId（例: p00001234-xxxxxxxxxx）
	 * @throws IllegalArgumentException
	 *             入力が未設定/形式不正
	 */
	static String toViewId(final String entityId) {
		return IdBridge.toViewId(entityId);
	}

	/**
	 * ViewId → EntityId 候補へ変換（桁復元・互換）。
	 *
	 * @param viewId
	 *            ViewId
	 * @return 復元候補の実ID（例: WE00001234）
	 * @throws IllegalArgumentException
	 *             入力が未設定/形式不正
	 */
	static String toEntityId(final String viewId) {
		return IdBridge.toEntityId(viewId, PREFIX, TOTAL);
	}

	/**
	 * ViewId と EntityId の照合（改ざん検知）。
	 *
	 * @param viewId
	 *            ViewId
	 * @param entityId
	 *            実ID
	 * @throws IllegalArgumentException
	 *             不一致/未設定
	 */
	static void assertMatches(final String viewId, final String entityId) {
		IdBridge.assertMatches(viewId, entityId);
	}
	/* ===== [public/protected] END ===== */
}
