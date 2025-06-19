package com.odit.backend.domain.image.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class ImageException extends BusinessException {
	public ImageException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
