package com.odit.backend.domain.place.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class PlaceException extends BusinessException {
	public PlaceException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
