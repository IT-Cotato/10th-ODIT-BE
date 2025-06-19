package com.odit.backend.domain.event.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class EventException extends BusinessException {
	public EventException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
