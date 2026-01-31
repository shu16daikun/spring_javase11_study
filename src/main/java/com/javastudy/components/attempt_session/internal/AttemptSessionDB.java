/** */
package com.javastudy.components.attempt_session.internal;

final class AttemptSessionDB {
	static final String TABLE = "attempt_session";

	static final class AttemptSessionColumn {
		static final String ID = "id";
		static final String USER_ID = "user_id";
		static final String SANKOU_BOOK_ID = "sankou_book_id";
		static final String STARTED_AT = "started_at";
		static final String FINISHED_AT = "finished_at";
	}

	static final class AttemptSessionIdParam {
		static final String SEQUENCE = "public.as_id_seq";
		static final String PREFIX = "AS";
		static final int PAD = 10; // "AS" + 10桁 = varchar(12)
	}
}
