package com.javastudy.components.chapter.api.exception;

import com.exception.contents.MyRuntimeException;
import com.exception.error_code.ErrorCode;

/** 章コンポーネントの例外 */
public final class ChapterException extends MyRuntimeException {
	public ChapterException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ChapterException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	public ChapterException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public ChapterException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}
}
