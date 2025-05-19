package com.adit.backend.domain.place.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserPlaceImageResponseDto(
	@NotNull(message = "사용자 장소 이미지 ID는 필수입니다.")
	Long userPlaceImageId,
	@NotNull(message = "사용자 장소 ID는 필수입니다.")
	Long userPlaceId,
	@NotBlank(message = "URL은 비어 있을 수 없습니다.")
	String url) {
}
