package com.odit.backend.domain.event.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserEventImageResponseDto(
	@NotNull(message = "이벤트 이미지 ID는 필수입니다.")
	Long id,
	@NotNull(message = "사용자 이벤트 ID는 필수입니다.")
	Long userEventId,
	@NotBlank(message = "URL은 비어 있을 수 없습니다.")
	String url) {
}
