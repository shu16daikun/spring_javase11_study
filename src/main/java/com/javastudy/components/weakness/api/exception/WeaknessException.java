// com.javastudy.components.weakness.api.exception.WeaknessException
package com.javastudy.components.weakness.api.exception;

import com.exception.contents.MyRuntimeException;
import com.exception.error_code.ErrorCode;

/**
 * 弱点分析ドメインの例外。
 *
 * <p>
 * 画面表示は messageKey から解決（ErrorCode 経由）し、内部構造は出さない。
 */
public final class WeaknessException extends MyRuntimeException {
	public WeaknessException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public WeaknessException(final ErrorCode errorCode, final String debugMessage) {
		super(errorCode, debugMessage);
	}

	public WeaknessException(final ErrorCode errorCode, final Throwable cause) {
		super(errorCode, cause);
	}

	public WeaknessException(
		final ErrorCode errorCode,
		final String debugMessage,
		final Throwable cause) {
		super(errorCode, debugMessage, cause);
	}
}
