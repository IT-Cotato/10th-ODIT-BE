package com.odit.backend.global.error;

public interface ErrorCode {
	String name();

	int getHttpStatus();

	String getMessage();
}
