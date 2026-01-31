package com.javastudy.components.kurohon_questions.internal;

/* KurohonQuestions ID規約定数 */
final class KurohonQuestionsIdConstants {

	// 機能：インスタンス化防止
	private KurohonQuestionsIdConstants() {
	}

	// 機能：プレフィックス（ID列）
	private static final String PREFIX = "KQ";
	// 機能：総桁数（ID列：KQ + 8桁連番）
	private static final int TOTAL_LENGTH = 10;
	// 機能：数値部桁数
	private static final int NUMERIC_LENGTH = 8;
	// 機能：検証用正規表現（ID列）
	private static final String REGEX = "^KQ\\d{8}$";

	// 機能：参考：別表現（参考書コード4 + 章No2 + 問No2）
	private static final int BOOK_CODE_LENGTH = 4; // 例：SAJS
	private static final int CHAPTER_NO_LENGTH = 2; // 例：01
	private static final int QUESTION_NO_LENGTH = 2; // 例：01

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

	static final int getQuestionNoLength() {
		return QUESTION_NO_LENGTH;
	}
}
