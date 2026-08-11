package com.javastudy.components.answer_records.api.exception;

import com.my.exception.MyRuntimeException;
import com.my.exception.error_code.ErrorCode;

/** 解答履歴例外 */
public final class AnswerRecordsException extends MyRuntimeException {
	public AnswerRecordsException(ErrorCode errorCode) {
		super(errorCode);
	}

	public AnswerRecordsException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	public AnswerRecordsException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public AnswerRecordsException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}
}
