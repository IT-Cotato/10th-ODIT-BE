package com.odit.backend.domain.auth.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class AuthException extends BusinessException {
	public AuthException(GlobalErrorCode errorCode) {
		super(errorCode);
	}

}
