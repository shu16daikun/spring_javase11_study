// com.javastudy.components.kurohon_questions.internal.KurohonQuestionsDB
package com.javastudy.components.kurohon_questions.internal;

final class KurohonQuestionsDB {
	static final String TABLE = "kurohon_questions";

	static final class KurohonQuestionsColumn {
		static final String ID = "id";
		static final String CHAPTER_ID = "chapter_id";
		static final String QUESTION_NO = "question_no";
		static final String QUESTION_HTML = "question_html";
		static final String CORRECT_OPTION = "correct_option";
		static final String EXPLANATION_HTML = "explanation_html";
		static final String ANSWER_COUNT_MAX = "answer_count_max";
		static final String OPTION_COUNT = "option_count";
		static final String SANKOU_BOOK_ID = "sankou_book_id";

		private KurohonQuestionsColumn() {
		}
	}

	static final class KurohonQuestionsIdParam {
		private KurohonQuestionsIdParam() {
		}

		static final String SEQUENCE = "public.kq_id_seq";
		static final String PREFIX = KurohonQuestionsIdConstants.getPrefix();
		static final int PAD = KurohonQuestionsIdConstants.getNumericLength(); // 8桁
	}
}
