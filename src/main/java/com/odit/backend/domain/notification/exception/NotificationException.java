package com.odit.backend.domain.notification.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class NotificationException extends BusinessException {
	public NotificationException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
