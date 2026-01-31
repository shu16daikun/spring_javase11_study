// com.javastudy.components.sankou_book_color.internal.SankouBookColorDB
package com.javastudy.components.sankou_book_color.internal;

final class SankouBookColorDB {

	/** スキーマ */
	static final String SCHEMA = "public";

	/** 物理テーブル名 */
	static final String TABLE = "sankou_book_color";

	/** FQN（schema.table） */
	static final String TABLE_FQN = SCHEMA + "." + TABLE;

	static final class SankouBookColorColumn {
		static final String ID = "id";
		static final String NAME = "name";

		private SankouBookColorColumn() {
		}
	}

	/** ID採番（SEQ名はDB、prefix/PADは IdConstants に委譲） */
	static final class SankouBookColorIdParam {
		private SankouBookColorIdParam() {
		}

		static final String SEQUENCE = SCHEMA + ".sc_id_seq";
		static final String PREFIX = SankouBookColorIdConstants.getPrefix(); // "SC"
		static final int PAD = SankouBookColorIdConstants.getNumericLength(); // 数値部 3 桁
	}

	private SankouBookColorDB() {
	}
}
