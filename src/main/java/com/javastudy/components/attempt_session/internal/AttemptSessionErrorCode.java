// com.javastudy.components.attempt_session.internal.AttemptSessionErrorCode
package com.javastudy.components.attempt_session.internal;

import com.exception.error_code.ErrorCode;
import com.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

/** 解答セッションのエラーコード定義 */
@AllArgsConstructor
public enum AttemptSessionErrorCode implements ErrorCode {

	// 400 Bad Request
	BLANK_ID("AS-400-01", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),

	// 404 Not Found
	NOT_ENTITY("AS-404-01", "error.attemptsession.notfound", HttpStatusCode.NOT_FOUND),

	// 409 Conflict（二重終了など衝突／ID重複）
	ALREADY_FINISHED("AS-409-01", "error.attemptsession.alreadyfinished", HttpStatusCode.CONFLICT),
	DUPLICATE_ID("AS-409-02", "error.common.duplicateid", HttpStatusCode.CONFLICT); // ← 追加

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

	/* ===== [debug messages: AttemptSession] START ===== */
	public static final class AttemptSessionDbgMsg {
		private AttemptSessionDbgMsg() {
		}

		/** BLANK_ID */
		public static String blankId() {
			return "attemptSessionId is blank";
		}

		public static String blankId(final String reason) {
			return String.format("attemptSessionId is blank: reason=%s", safe(reason));
		}

		/** NOT_ENTITY */
		public static String notEntity(final String id) {
			return String.format("attempt session not found: id=%s", safe(id));
		}

		/** ALREADY_FINISHED */
		public static String alreadyFinished(final String id) {
			return String.format("attempt session already finished: id=%s", safe(id));
		}

		/** DUPLICATE_ID */
		public static String duplicateId(final String id) {
			return String.format("duplicate id: id=%s", safe(id));
		}

		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: AttemptSession] END ===== */
}
