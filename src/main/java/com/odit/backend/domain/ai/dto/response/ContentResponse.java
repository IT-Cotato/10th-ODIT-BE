package com.odit.backend.domain.ai.dto.response;

import com.odit.backend.domain.ai.enums.ContentType;


public record ContentResponse(
	String name,
	ContentType type,
	String location,
	String period
) {
}
