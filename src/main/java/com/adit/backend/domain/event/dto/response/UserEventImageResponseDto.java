package com.adit.backend.domain.event.dto.response;

import com.adit.backend.domain.image.entity.UserEventImage;

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
	public static UserEventImageResponseDto of(UserEventImage userEventImage) {
		return UserEventImageResponseDto.builder()
			.id(userEventImage.getId())
			.userEventId(userEventImage.getUserEvent().getId())
			.url(userEventImage.getUrl())
			.build();
	}
}
