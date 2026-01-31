package com.javastudy.components.kurohon_questions.api.exception;

import com.exception.contents.MyRuntimeException;
import com.exception.error_code.ErrorCode;

/** 黒本問題ドメイン例外 */
public final class KurohonQuestionsException extends MyRuntimeException {
	public KurohonQuestionsException(ErrorCode errorCode) {
		super(errorCode);
	}

	public KurohonQuestionsException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	public KurohonQuestionsException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public KurohonQuestionsException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}
}
