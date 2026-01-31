// com.javastudy.components.kurohon_questions.internal.KurohonQuestionsErrorCode
package com.javastudy.components.kurohon_questions.internal;

import com.exception.error_code.ErrorCode;
import com.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
enum KurohonQuestionsErrorCode implements ErrorCode {

	// 400 Bad Request
	BLANK_ID("KQ-400-02", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),
	DTO_MISMATCH("KQ-400-01", "error.kurohon.questions.dto.mismatch", HttpStatusCode.BAD_REQUEST),

	// 404 Not Found
	NOT_ENTITY("KQ-404-01", "error.kurohon.questions.notfound", HttpStatusCode.NOT_FOUND),

	// 409 Conflict
	DUPLICATE_QUESTION_NO("KQ-409-01", "error.kurohon.questions.duplicateno",
		HttpStatusCode.CONFLICT),
	IN_USE("KQ-409-02", "error.common.badrequest", HttpStatusCode.CONFLICT), // ★追加（使用中のため削除不可）
	DUPLICATE_ID("KQ-409-03", "error.common.duplicateid", HttpStatusCode.CONFLICT);

	// 422 Unprocessable Entity
	private final String code;
	private final String messageKey;
	private final HttpStatusCode status;

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessageKey() {
		return this.messageKey;
	}

	@Override
	public HttpStatusCode getHttpStatus() {
		return this.status;
	}

	/* ===== [debug messages: KurohonQuestions] START ===== */
	public static final class KurohonQuestionsDbgMsg {
		private KurohonQuestionsDbgMsg() {
		}

		/** BLANK_ID */
		public static String blankId() {
			return "kurohonQuestions id is blank";
		}

		public static String blankId(final String reason) {
			return String.format("kurohonQuestions id is blank: reason=%s", safe(reason));
		}

		/** DTO_MISMATCH */
		public static String dtoMismatch() {
			return "dto mismatch";
		}

		public static String dtoMismatch(final String reason) {
			return String.format("dto mismatch: reason=%s", safe(reason));
		}

		/** NOT_ENTITY */
		public static String notEntity(final String id) {
			return String.format("kurohon question not found: id=%s", safe(id));
		}

		public static String notEntity(final String key, final String value) {
			return String.format("kurohon question not found: %s=%s", safe(key), safe(value));
		}

		/** DUPLICATE_QUESTION_NO */
		public static String duplicateQuestionNo(final String chapterId, final String questionNo) {
			return String.format("duplicate question no: chapterId=%s, questionNo=%s",
				safe(chapterId), safe(questionNo));
		}

		public static String duplicateQuestionNo(final String questionNo) {
			return String.format("duplicate question no: questionNo=%s", safe(questionNo));
		}

		/** 422s */
		public static String invalidCorrectOption(final String correct) {
			return String.format("invalid correct option: correct=%s", safe(correct));
		}

		public static String invalidOptionCount(final int expectedMin, final int actual) {
			return String.format("invalid option count: expectedMin=%d, actual=%d", expectedMin,
				actual);
		}

		public static String duplicateId(final String id) {
			return String.format("duplicate id: id=%s", safe(id));
		}

		/** IN_USE */
		public static String inUse(final String id) {
			return String.format("kurohon question is in use: id=%s", safe(id));
		}

		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: KurohonQuestions] END ===== */
}
