// com/javastudy/components/sankou_book_color/internal/SankouBookColorErrorCode.java
package com.javastudy.components.sankou_book_color.internal;

import com.my.exception.error_code.ErrorCode;
import com.my.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

/** 参考書カラーのエラーコード定義 */
@AllArgsConstructor
enum SankouBookColorErrorCode implements ErrorCode {

	// 400 Bad Request
	BLANK_ID("BOOKCOLOR-400-01", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),
	BLANK_NAME("BOOKCOLOR-400-02", "error.sankoubookcolor.name.blank", HttpStatusCode.BAD_REQUEST),

	// 404 Not Found
	NOT_ENTITY("BOOKCOLOR-404-01", "error.common.notfound", HttpStatusCode.NOT_FOUND),

	// 409 Conflict
	DUPLICATE_NAME("BOOKCOLOR-409-01", "error.sankoubookcolor.name.duplicate",
		HttpStatusCode.CONFLICT),
	/** 使用中のため削除不可 */
	IN_USE("BOOKCOLOR-409-02", "error.sankoubookcolor.inuse", HttpStatusCode.CONFLICT),
	DUPLICATE_ID("BOOKCOLOR-409-03", "error.common.duplicateid", HttpStatusCode.CONFLICT);

	private final String code; // 内部ユニーク
	private final String messageKey; // propertiesキー
	private final HttpStatusCode status; // HTTPステータス

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

	/* ===== [debug messages: SankouBookColor] START ===== */
	/**
	 * 参考書カラー用デバッグメッセージ。
	 * ルール: ErrorCode定数名とメソッド名を合わせる（lowerCamel）。
	 */
	public static final class SankouBookColorDbgMsg {
		private SankouBookColorDbgMsg() {
		}

		/** BLANK_ID */
		public static String blankId() {
			return "sankouBookColor id is blank";
		}

		public static String blankId(final String reason) {
			return String.format("sankouBookColor id is blank: reason=%s", safe(reason));
		}

		/** BLANK_NAME */
		public static String blankName() {
			return "name is blank";
		}

		public static String blankName(final String reason) {
			return String.format("name is blank: reason=%s", safe(reason));
		}

		/** NOT_ENTITY */
		public static String notEntity(final String id) {
			return String.format("sankou book color not found: id=%s", safe(id));
		}

		public static String notEntity(final String key, final String value) {
			return String.format("sankou book color not found: %s=%s", safe(key), safe(value));
		}

		/** DUPLICATE_NAME */
		public static String duplicateName(final String name) {
			return String.format("duplicate color name: name=%s", safe(name));
		}

		public static String duplicateId(final String id) {
			return String.format("duplicate id: id=%s", safe(id));
		}

		/** IN_USE */
		public static String inUse(final String id) {
			return String.format("color is in use: id=%s", safe(id));
		}

		/* helper */
		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: SankouBookColor] END ===== */
}
