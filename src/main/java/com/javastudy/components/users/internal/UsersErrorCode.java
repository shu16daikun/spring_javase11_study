// com.javastudy.components.users.internal.UsersErrorCode
package com.javastudy.components.users.internal;

import com.exception.error_code.ErrorCode;
import com.exception.error_code.HttpStatusCode;

import lombok.AllArgsConstructor;

/**
 * 【機能】Usersドメインのエラーコード定義
 *
 * <p>
 * UIには userCode（= code）と messageKey から解決した文言のみ提示。
 */
@AllArgsConstructor
enum UsersErrorCode implements ErrorCode {

	/* 404 Not Found */
	NOT_ENTITY("USR-404-01", "error.common.notfound", HttpStatusCode.NOT_FOUND),

	/* 400 Bad Request */
	BLANK_USERNAME("USR-400-01", "error.common.badrequest", HttpStatusCode.BAD_REQUEST),
	USERNAME_PATTERN_VIOLATION("USR-400-02", "error.user.username.pattern",
		HttpStatusCode.BAD_REQUEST),

	/* 409 Conflict */
	DUPLICATE_USERNAME("USR-409-01", "error.common.badrequest", HttpStatusCode.CONFLICT),
	IN_USE("USR-409-02", "error.common.badrequest", HttpStatusCode.CONFLICT); // ★追加（使用中のため削除不可）

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

	/* （必要に応じて）デバッグメッセージ */
	public static final class UsersDbgMsg {
		private UsersDbgMsg() {
		}

		public static String notEntity(final String id) {
			return "user not found: id=" + safe(id);
		}

		public static String blankUsername() {
			return "username is blank";
		}

		public static String duplicateUsername(final String name) {
			return "duplicate username: " + safe(name);
		}

		public static String inUse(final String userId) {
			return "user is in use: id=" + safe(userId);
		}

		private static String safe(final String s) {
			return s == null ? "<null>" : s;
		}
	}
}
