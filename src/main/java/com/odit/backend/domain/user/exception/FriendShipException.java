package com.odit.backend.domain.user.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class FriendShipException extends BusinessException {
	public FriendShipException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
