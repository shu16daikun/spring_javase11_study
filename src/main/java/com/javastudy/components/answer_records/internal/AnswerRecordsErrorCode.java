// com.javastudy.components.answer_records.internal.AnswerRecordsErrorCode
package com.javastudy.components.answer_records.internal;

import com.my.exception.error_code.ErrorCode;
import com.my.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

/** 解答履歴エラーコード */
@AllArgsConstructor
enum AnswerRecordsErrorCode implements ErrorCode {

	// 400 Bad Request（DTO不整合／選択肢系のバリデーション）
	QUESTIONS_MISMATCH("AR-400-01", "error.kurohon.questions.dto.mismatch",
		HttpStatusCode.BAD_REQUEST),
	SELECTED_OPTION_PATTERN("AR-400-02", "error.answer.selectedoption.pattern",
		HttpStatusCode.BAD_REQUEST),
	SELECTED_OPTION_UNKNOWN_CONFLICT("AR-400-03", "error.answer.selectedoption.unknown",
		HttpStatusCode.BAD_REQUEST),
	SELECTED_OPTION_MAX("AR-400-04", "error.answer.selectedoption.max", HttpStatusCode.BAD_REQUEST),

	// 404 Not Found
	NOT_ENTITY("AR-404-01", "error.answer.notfound", HttpStatusCode.NOT_FOUND),

	// 409 Conflict（ID重複）
	DUPLICATE_ID("AR-409-01", "error.common.duplicateid", HttpStatusCode.CONFLICT); // ← 追加

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

	/* ===== [debug messages: AnswerRecords] START ===== */
	public static final class AnswerRecordsDbgMsg {
		private AnswerRecordsDbgMsg() {
		}

		/** QUESTIONS_MISMATCH */
		public static String questionsMismatch() {
			return "questions dto mismatch";
		}

		public static String questionsMismatch(final String expected, final String actual) {
			return String.format("questions dto mismatch: expected=%s, actual=%s", safe(expected),
				safe(actual));
		}

		/** SELECTED_OPTION_PATTERN */
		public static String selectedOptionPattern(final String pattern, final String actual) {
			return String.format("selected option pattern invalid: pattern=%s, actual=%s",
				safe(pattern), safe(actual));
		}

		/** SELECTED_OPTION_UNKNOWN_CONFLICT */
		public static String
			selectedOptionUnknownConflict(final String selected, final String correct) {
			return String.format("selected option unknown conflict: selected=%s, correct=%s",
				safe(selected), safe(correct));
		}

		/** SELECTED_OPTION_MAX */
		public static String selectedOptionMax(final int max, final int actual) {
			return String.format("selected option max exceeded: max=%d, actual=%d", max, actual);
		}

		/** NOT_ENTITY */
		public static String notEntity(final String id) {
			return String.format("answer record not found: id=%s", safe(id));
		}

		public static String notEntity(final String key, final String value) {
			return String.format("answer record not found: %s=%s", safe(key), safe(value));
		}

		/** DUPLICATE_ID */
		public static String duplicateId(final String id) {
			return String.format("duplicate id: id=%s", safe(id));
		}

		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: AnswerRecords] END ===== */
}
