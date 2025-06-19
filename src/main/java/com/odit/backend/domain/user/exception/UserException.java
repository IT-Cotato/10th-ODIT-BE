package com.odit.backend.domain.user.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class UserException extends BusinessException {
	public UserException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
