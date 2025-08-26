package com.odit.backend.domain.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserPlaceImageResponseDto(
	@Schema(
		description = "userPlaceImage Id",
		example = "1"
	)
	Long userPlaceImageId,
	@Schema(
		description = "userPlace Id",
		example = "1"
	)
	Long userPlaceId,
	@Schema(
		description = "업데이트 된 이미지 URL",
		example = "[\"https://example.com/image2.jpg\"]"
	)
	String url) {
}
