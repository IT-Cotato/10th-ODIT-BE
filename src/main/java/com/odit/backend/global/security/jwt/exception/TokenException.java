package com.odit.backend.global.security.jwt.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class TokenException extends BusinessException {
	public TokenException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
