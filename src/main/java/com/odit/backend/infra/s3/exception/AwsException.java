package com.odit.backend.infra.s3.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class AwsException extends BusinessException {

	public AwsException(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
