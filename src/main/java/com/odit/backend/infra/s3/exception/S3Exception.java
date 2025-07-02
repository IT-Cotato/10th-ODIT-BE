package com.odit.backend.infra.s3.exception;

import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.global.error.exception.BusinessException;

public class S3Exception extends BusinessException {

	public S3Exception(GlobalErrorCode errorCode) {
		super(errorCode);
	}
}
