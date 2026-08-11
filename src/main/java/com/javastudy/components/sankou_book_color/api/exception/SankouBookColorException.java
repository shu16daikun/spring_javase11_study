package com.javastudy.components.sankou_book_color.api.exception;

import com.my.exception.MyRuntimeException;
import com.my.exception.error_code.ErrorCode;

/** 参考書カラー例外 */
public final class SankouBookColorException extends MyRuntimeException {
	public SankouBookColorException(ErrorCode errorCode) {
		super(errorCode);
	}

	public SankouBookColorException(ErrorCode errorCode, String debugMessage) {
		super(errorCode, debugMessage);
	}

	public SankouBookColorException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public SankouBookColorException(ErrorCode errorCode, String debugMessage, Throwable cause) {
		super(errorCode, debugMessage, cause);
	}
}
