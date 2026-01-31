// com.javastudy.components.sankou_books.internal.SankouBooksIdConstants
package com.javastudy.components.sankou_books.internal;

/**
 * 【機能】SankouBooks ID規約定数
 *
 * <p>
 * 目的：ID列の形式（接頭辞/桁/正規表現等）を一元管理。
 *
 * <h2>契約</h2>
 *
 * <ul>
 * <li>ID列接頭辞：<code>SA</code>
 * <li>総桁数：7（例：SA + 5桁連番）
 * <li>正規表現：<code>^SA\\d{5}$</code>
 * <li>資格種別コード：大文字2桁（参考用途）
 * </ul>
 *
 * <h2>設計メモ</h2>
 *
 * <ul>
 * <li>⚠ <em>SankouBooksDB.SankouBooksIdParam</em> の採番設定（例：SB/12桁）と整合を取ること。
 * </ul>
 */
final class SankouBooksIdConstants {

	private SankouBooksIdConstants() {
	}

	// 機能：プレフィックス（ID列）
	private static final String PREFIX = "SA";
	// 機能：総桁数（ID列：SA + 5桁連番）
	private static final int TOTAL_LENGTH = 7;
	// 機能：数値部桁数
	private static final int NUMERIC_LENGTH = 5;
	// 機能：検証用正規表現（ID列）
	private static final String REGEX = "^SA\\d{5}$";

	// 機能：参考「資格種別コード」長（例：JS/JB/JG）
	private static final int QUAL_CODE_LENGTH = 2;
	// 機能：資格種別コード検証用正規表現（大文字2桁）
	private static final String QUAL_CODE_REGEX = "^[A-Z]{2}$";

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

	static final int getQualCodeLength() {
		return QUAL_CODE_LENGTH;
	}

	static final String getQualCodeRegex() {
		return QUAL_CODE_REGEX;
	}
}
