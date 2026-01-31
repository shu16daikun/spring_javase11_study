package com.javastudy.components.chapter.internal;

/* Chapter ID規約定数 */
final class ChapterIdConstants {

	// 機能：インスタンス化防止
	private ChapterIdConstants() {
	}

	// 機能：プレフィックス（ID列）
	private static final String PREFIX = "CH";
	// 機能：総桁数（ID列：CH + 8桁連番）
	private static final int TOTAL_LENGTH = 10;
	// 機能：数値部桁数
	private static final int NUMERIC_LENGTH = 8;
	// 機能：検証用正規表現（ID列）
	private static final String REGEX = "^CH\\d{8}$";

	// 機能：参考：章No（2桁）と参考書コード（4桁）を組み合わせた表現も仕様に登場
	private static final int BOOK_CODE_LENGTH = 4; // 例：SAJS
	private static final int CHAPTER_NO_LENGTH = 2; // 例：01

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

	static final int getBookCodeLength() {
		return BOOK_CODE_LENGTH;
	}

	static final int getChapterNoLength() {
		return CHAPTER_NO_LENGTH;
	}
}
