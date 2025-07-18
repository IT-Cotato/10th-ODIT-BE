package com.odit.backend.domain.event.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserEventImageResponseDto(
	@Schema(
		description = "Event ID",
		example = "2"
	)
	Long id,
	@Schema(
		description = "User Event ID",
		example = "2"
	)
	Long userEventId,
	@Schema(
		description = "업데이트 된 이미지 URL",
		example = "[\"https://example.com/image2.jpg\"]"
	)
	String url) {
}
