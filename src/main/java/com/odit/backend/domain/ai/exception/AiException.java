package com.odit.backend.domain.ai.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class AiException extends BusinessException {
	public AiException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
