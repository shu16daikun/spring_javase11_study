package com.javastudy.components.attempt_session.internal;

final class AttemptSessionIdConstants {

	// 機能：インスタンス化防止
	private AttemptSessionIdConstants() {
	}

	// 機能：プレフィックス（ID列）
	private static final String PREFIX = "AS";
	// 機能：総桁数（ID列：AR + 8桁連番）
	private static final int TOTAL_LENGTH = 12;
	// 機能：数値部桁数
	private static final int NUMERIC_LENGTH = 10;
	// 機能：検証用正規表現（ID列）
	private static final String REGEX = "^AS\\d{10}$";

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
