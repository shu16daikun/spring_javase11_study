// com.javastudy.components.weakness.internal.WeaknessDB
package com.javastudy.components.weakness.internal;

final class WeaknessDB {

	/** スキーマ */
	static final String SCHEMA = "public";

	/** 物理テーブル名 */
	static final String TABLE = "weakness";

	/** FQN（schema.table） */
	static final String TABLE_FQN = SCHEMA + "." + TABLE;

	static final class WeaknessColumn {
		static final String ID = "id";
		static final String USER_ID = "user_id";
		static final String CORRECT_RATE = "correct_rate";
		static final String CHAPTER_ID = "chapter_id";
		static final String SANKOU_BOOK_ID = "sankou_book_id";
		static final String KQ_ID = "kurohon_question_id";
		static final String TOTAL_ATTEMPTS = "total_attempts";
		static final String CORRECT_COUNT = "correct_count";
		static final String WRONG_COUNT = "wrong_count";
		static final String LAST_ANSWERED_AT = "last_answered_at";
		static final String LAST_SELECTED_OPTION = "last_selected_option";
		static final String LAST_IS_CORRECT = "last_is_correct";

		private WeaknessColumn() {
		}
	}

	/** ID採番（SEQ名はDB、prefix/PADは IdConstants に委譲） */
	static final class WeaknessIdParam {
		private WeaknessIdParam() {
		}

		static final String SEQUENCE = SCHEMA + ".we_id_seq";
		static final String PREFIX = WeaknessIdConstants.getPrefix(); // "WE"
		static final int PAD = WeaknessIdConstants.getNumericLength(); // 数値部の桁数（例：8）
	}

	private WeaknessDB() {
	}
}
