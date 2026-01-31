// com.javastudy.components.sankou_books.api.exception.SankouBooksException
package com.javastudy.components.sankou_books.api.exception;

import com.exception.contents.MyRuntimeException;
import com.exception.error_code.ErrorCode;

/**
 * 【機能】参考書ドメイン例外
 *
 * <p>
 * 目的：SankouBooks ユースケースのドメイン/ポリシー違反を表現。
 *
 * <h2>方針</h2>
 *
 * <ul>
 * <li>ErrorCode は必須（画面には短い userCode のみ。内部情報は出さない）。
 * <li><code>debugMessage</code> はログ専用（UIへは非表示）。
 * </ul>
 */
public final class SankouBooksException extends MyRuntimeException {

	/* ===== [public/protected] START ===== */

	/** ErrorCodeのみの基本コンストラクタ。 */
	public SankouBooksException(ErrorCode errorCode) {
		super(errorCode);
	}

	/** デバッグ用文言を付与（ログ専用）。 */
	public SankouBooksException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	/** 原因例外をラップ。 */
	public SankouBooksException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/** デバッグ文言＋原因例外の複合。 */
	public SankouBooksException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}

	/* ===== [public/protected] END ===== */
	/* ===== [private] START ===== */
	// なし
	/* ===== [private] END ===== */
}
