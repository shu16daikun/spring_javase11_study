// com.javastudy.components.sankou_books.internal.SankouBooksErrorCode
package com.javastudy.components.sankou_books.internal;

import com.exception.error_code.ErrorCode;
import com.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
enum SankouBooksErrorCode implements ErrorCode {

	// 400 Bad Request
	BLANK_ID("BOOK-400-01", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),
	BLANK_NAME("BOOK-400-02", "error.sankoubooks.name.blank", HttpStatusCode.BAD_REQUEST),
	BLANK_COLOR("BOOK-400-03", "error.sankoubooks.color.blank", HttpStatusCode.BAD_REQUEST),

	// 404 Not Found
	NOT_ENTITY("BOOK-404-01", "error.common.notfound", HttpStatusCode.NOT_FOUND),

	// 409 Conflict
	DUPLICATE_NAME("BOOK-409-01", "error.sankoubooks.name.duplicate", HttpStatusCode.CONFLICT),
	IN_USE("BOOK-409-02", "error.sankoubooks.inuse", HttpStatusCode.CONFLICT),
	DUPLICATE_ID("BOOK-409-03", "error.common.duplicateid", HttpStatusCode.CONFLICT); // ← 追加

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

	/* ===== [debug messages: SankouBooks] START ===== */
	public static final class SankouBooksDbgMsg {
		private SankouBooksDbgMsg() {
		}

		public static String blankId() {
			return "sankouBooks id is blank";
		}

		public static String blankId(final String reason) {
			return String.format("sankouBooks id is blank: reason=%s", safe(reason));
		}

		public static String blankName() {
			return "name is blank";
		}

		public static String blankName(final String reason) {
			return String.format("name is blank: reason=%s", safe(reason));
		}

		public static String blankColor() {
			return "colorViewId is blank";
		}

		public static String blankColor(final String reason) {
			return String.format("colorViewId is blank: reason=%s", safe(reason));
		}

		public static String notEntity(final String id) {
			return String.format("book not found: id=%s", safe(id));
		}

		public static String notEntity(final String key, final String value) {
			return String.format("book not found: %s=%s", safe(key), safe(value));
		}

		public static String duplicateName(final String name) {
			return String.format("duplicate book name: name=%s", safe(name));
		}

		public static String inUse(final String id) {
			return String.format("book is in use: id=%s", safe(id));
		}

		public static String duplicateId(final String id) {
			return String.format("duplicate id: id=%s", safe(id));
		}

		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: SankouBooks] END ===== */
}
