package com.odit.backend.infra.async.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class AsyncException extends BusinessException {

	public AsyncException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
