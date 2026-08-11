// com.javastudy.components.users.api.exception.UsersException
package com.javastudy.components.users.api.exception;

import com.my.exception.MyRuntimeException;
import com.my.exception.error_code.ErrorCode;

/**
 * 【機能】Usersドメイン共通例外
 *
 * <p>
 * 目的：Usersユースケースにおけるビジネス／バリデーション違反等を表現する。
 *
 * <h2>方針</h2>
 *
 * <ul>
 * <li>ErrorCode は必須（UI には短い <em>userCode</em> のみを提示し、内部構造は出さない）。
 * <li><code>debugMessage</code> はログ専用（画面には出さない）。
 * </ul>
 *
 * <h2>利用例</h2>
 *
 * <pre>{@code
 * throw new UsersException(UsersErrorCode.USERNAME_CONFLICT);
 * }</pre>
 */
public final class UsersException extends MyRuntimeException {
	/* ===== [public/protected] START ===== */

	/** ErrorCodeのみを持つ基本コンストラクタ。 */
	public UsersException(ErrorCode errorCode) {
		super(errorCode);
	}

	/** 追加のデバッグ文言を付与（ログ専用）。 */
	public UsersException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	/** 原因例外をラップ。 */
	public UsersException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/** デバッグ文言＋原因例外を付与。 */
	public UsersException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}

	/* ===== [public/protected] END ===== */
	/* ===== [private] START ===== */
	// なし（内部状態は親クラスに委譲）
	/* ===== [private] END ===== */
}
