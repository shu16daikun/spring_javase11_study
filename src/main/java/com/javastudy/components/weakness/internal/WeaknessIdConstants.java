// com.javastudy.components.weakness.internal.WeaknessIdConstants
package com.javastudy.components.weakness.internal;

/**
 * Weakness ID 規約定数。
 *
 * <p>
 * 形式: {@code WE}<digits8>（例: WE00001234）。検証は {@link #getRegex()} 参照。
 *
 * <ul>
 * <li>PREFIX: {@code WE}
 * <li>TOTAL_LENGTH: 10（接頭辞2 + 数値8）
 * <li>NUMERIC_LENGTH: 8
 * </ul>
 */
final class WeaknessIdConstants {

	/* ===== [private] START ===== */
	/** インスタンス化防止 */
	private WeaknessIdConstants() {
	}

	/** プレフィックス */
	private static final String PREFIX = "WE";

	/** 総桁数（WE + 8桁連番） */
	private static final int TOTAL_LENGTH = 10;

	/** 数値部桁数 */
	private static final int NUMERIC_LENGTH = 8;

	/** 検証用正規表現 */
	private static final String REGEX = "^WE\\d{8}$";

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	static String getPrefix() {
		return PREFIX;
	}

	static int getTotalLength() {
		return TOTAL_LENGTH;
	}

	static int getNumericLength() {
		return NUMERIC_LENGTH;
	}

	static String getRegex() {
		return REGEX;
	}
	/* ===== [public/protected] END ===== */
}
