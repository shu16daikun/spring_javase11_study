package com.javastudy.components.answer_records.internal;

/* AnswerRecords ID規約定数 */
final class AnswerRecordsIdConstants {

	// 機能：インスタンス化防止
	private AnswerRecordsIdConstants() {
	}

	// 機能：プレフィックス（ID列）
	private static final String PREFIX = "AR";
	// 機能：総桁数（ID列：AR + 8桁連番）
	private static final int TOTAL_LENGTH = 10;
	// 機能：数値部桁数
	private static final int NUMERIC_LENGTH = 8;
	// 機能：検証用正規表現（ID列）
	private static final String REGEX = "^AR\\d{8}$";

	// 機能：参考：複合表現（Users6 + Questions10 + 解答回数3 を含む21桁）
	private static final int COMPOSITE_TOTAL_LENGTH = 21;

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

	static final int getCompositeTotalLength() {
		return COMPOSITE_TOTAL_LENGTH;
	}
}
