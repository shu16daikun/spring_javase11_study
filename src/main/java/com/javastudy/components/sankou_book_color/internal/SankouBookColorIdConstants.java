package com.javastudy.components.sankou_book_color.internal;

/* SankouBookColor ID規約定数 */
final class SankouBookColorIdConstants {

	// 機能：インスタンス化防止
	private SankouBookColorIdConstants() {
	}

	// 機能：プレフィックス
	private static final String PREFIX = "SC";
	// 機能：総桁数（例：SC001）
	private static final int TOTAL_LENGTH = 5;
	// 機能：数値部桁数
	private static final int NUMERIC_LENGTH = 3;
	// 機能：検証用正規表現
	private static final String REGEX = "^SC\\d{3}$";

	// 機能：取得
	static final String getPrefix() {
		return PREFIX;
	}

	static final int getTotalLength() {
		return TOTAL_LENGTH;
	}

	static final int getNumericLength() {
		return NUMERIC_LENGTH;
	}

	static final String getRegex() {
		return REGEX;
	}
}
