// com/javastudy/components/chapter/internal/ChapterErrorCode.java
package com.javastudy.components.chapter.internal;

import com.exception.error_code.ErrorCode;
import com.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

/** 章コンポーネントのエラーコード定義 */
@AllArgsConstructor
enum ChapterErrorCode implements ErrorCode {

	// 400 Bad Request
	BLANK_ID("CH-400-01", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),

	// 404 Not Found
	NOT_ENTITY("CH-404-01", "error.chapter.notfound", HttpStatusCode.NOT_FOUND),

	// 409 Conflict（同一参考書内で章番号が重複／使用中削除不可／ID重複）
	DUPLICATE_NO("CH-409-01", "error.chapter.duplicateno", HttpStatusCode.CONFLICT),
	IN_USE("CH-409-02", "error.chapter.inuse", HttpStatusCode.CONFLICT),
	DUPLICATE_ID("CH-409-03", "error.common.duplicateid", HttpStatusCode.CONFLICT); // ← 追加

	private final String code; // 内部ユニーク（監視・集計用）
	private final String messageKey; // properties のキー
	private final HttpStatusCode status;// HTTP ステータス

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

	/* ===== [debug messages: Chapter] START ===== */
	/**
	 * 章コンポーネント用デバッグメッセージ。
	 * ルール: ErrorCode 定数名とメソッド名を合わせる（lowerCamel）。
	 */
	public static final class ChapterDbgMsg {
		private ChapterDbgMsg() {
		}

		/** BLANK_ID */
		public static String blankId() {
			return "chapterId is blank";
		}

		public static String blankId(final String reason) {
			return String.format("chapterId is blank: reason=%s", safe(reason));
		}

		/** NOT_ENTITY */
		public static String notEntity(final String id) {
			return String.format("chapter not found: id=%s", safe(id));
		}

		public static String notEntity(final String key, final String value) {
			return String.format("chapter not found: %s=%s", safe(key), safe(value));
		}

		/** DUPLICATE_NO */
		public static String duplicateNo(final String no) {
			return String.format("duplicate chapter no: no=%s", safe(no));
		}

		public static String duplicateNo(final String bookId, final String no) {
			return String.format("duplicate chapter no: bookId=%s, no=%s", safe(bookId), safe(no));
		}

		/** IN_USE */
		public static String inUse(final String id) {
			return String.format("chapter is in use: id=%s", safe(id));
		}

		/** DUPLICATE_ID */
		public static String duplicateId(final String id) {
			return String.format("duplicate id: id=%s", safe(id));
		}

		/* helper */
		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: Chapter] END ===== */
}
