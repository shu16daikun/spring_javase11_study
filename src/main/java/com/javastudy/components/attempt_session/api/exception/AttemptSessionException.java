package com.javastudy.components.attempt_session.api.exception;

import com.my.exception.MyRuntimeException;
import com.my.exception.error_code.ErrorCode;

/** 解答セッション例外 */
public final class AttemptSessionException extends MyRuntimeException {
	public AttemptSessionException(ErrorCode errorCode) {
		super(errorCode);
	}

	public AttemptSessionException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	public AttemptSessionException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public AttemptSessionException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}
}
