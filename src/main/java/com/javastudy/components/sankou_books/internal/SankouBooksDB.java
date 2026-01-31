// com.javastudy.components.sankou_books.internal.SankouBooksDB
package com.javastudy.components.sankou_books.internal;

final class SankouBooksDB {

	/** スキーマ */
	static final String SCHEMA = "public";

	/** 物理テーブル名 */
	static final String TABLE = "sankou_books";

	/** FQN（schema.table） */
	static final String TABLE_FQN = SCHEMA + "." + TABLE;

	static final class SankouBooksColumn {
		static final String ID = "id";
		static final String NAME = "name";
		static final String COLOR_ID = "color_id";

		private SankouBooksColumn() {
		}
	}

	/** ID採番（SEQ名はDB、prefix/PADは IdConstants に委譲） */
	static final class SankouBooksIdParam {
		private SankouBooksIdParam() {
		}

		// ★ sb ではなく sa
		static final String SEQUENCE = SCHEMA + ".sa_id_seq";
		static final String PREFIX = SankouBooksIdConstants.getPrefix(); // "SA"
		static final int PAD = SankouBooksIdConstants.getNumericLength(); // 数値部 5 桁
	}

	private SankouBooksDB() {
	}
}
