// com.javastudy.components.chapter.internal.ChapterDB
package com.javastudy.components.chapter.internal;

final class ChapterDB {
	static final String TABLE = "chapter";

	static final class ChapterColumn {
		static final String ID = "id";
		static final String NO = "no";
		static final String NAME = "name";
		static final String SANKOU_BOOK_ID = "sankou_book_id";

		private ChapterColumn() {
		}
	}

	static final class ChapterIdParam {
		private ChapterIdParam() {
		}

		static final String SEQUENCE = "public.ch_id_seq";
		static final String PREFIX = ChapterIdConstants.getPrefix();
		static final int PAD = ChapterIdConstants.getNumericLength(); // schemaの8桁に一致
	}
}
