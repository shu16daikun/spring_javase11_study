// com.javastudy.components.weakness.internal.WeaknessErrorCode
package com.javastudy.components.weakness.internal;

import com.exception.error_code.ErrorCode;
import com.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

/**
 * 弱点ドメイン用のErrorCode。
 *
 * <p>
 * 画面表示はメッセージキーで行い、内部構造は出さない（userCode方針）。
 */
@AllArgsConstructor
enum WeaknessErrorCode implements ErrorCode {

	/** 400: 入力不備（ID/引数欠落など） */
	BLANK_ID("WN-400-01", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),

	/** 404: 対象が見つからない（ID不正/未登録） */
	NOT_ENTITY("WN-404-01", "error.common.notfound", HttpStatusCode.NOT_FOUND);

	/* ===== [private] START ===== */
	private final String code;
	private final String messageKey;
	private final HttpStatusCode status;
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
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
	/* ===== [public/protected] END ===== */

	/* ===== [debug messages: Weakness] START ===== */
	/**
	 * 弱点ドメイン用デバッグメッセージ。
	 * ルール：ErrorCode定数名とメソッド名を対応（lowerCamel）。
	 */
	public static final class WeaknessDbgMsg {

		private WeaknessDbgMsg() {
		}

		/** BLANK_ID */
		public static String blankId() {
			return "weakness id is blank";
		}

		public static String blankId(final String reason) {
			return String.format("weakness id is blank: reason=%s", safe(reason));
		}

		/** NOT_ENTITY */
		public static String notEntity(final String id) {
			return String.format("weakness not found: id=%s", safe(id));
		}

		public static String notEntity(final String key, final String value) {
			return String.format("weakness not found: %s=%s", safe(key), safe(value));
		}

		/* helper */
		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
	/* ===== [debug messages: Weakness] END ===== */
}
