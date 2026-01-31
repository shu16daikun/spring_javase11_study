/** */
package com.javastudy.components.answer_records.internal;

/** */
final class AnswerRecordsDB {
	static final String TABLE = "answer_records";

	static final class AnswerRecordsColumn {

		static final String ID = "id";
		static final String USER_ID = "user_id";
		static final String QUESTION_ID = "kurohon_question_id";
		static final String SELECTED_OPTION = "selected_option";
		static final String IS_CORRECT = "is_correct";
		static final String ANSWERED_AT = "answered_at";
		static final String CHAPTER_ID = "chapter_id";
		static final String SANKOU_BOOK_ID = "sankou_book_id";
		static final String ATTEMPT_NO = "attempt_no";
		static final String ATTEMPT_SESSION_ID = "attempt_session_id";
	}

	static final class AnswerRecordsIdParam {
		static final String SEQUENCE = "public.ar_id_seq";
		static final String PREFIX = "AR";
		static final int PAD = 8; // "AR" + 8桁 = varchar(10)
	}
}
