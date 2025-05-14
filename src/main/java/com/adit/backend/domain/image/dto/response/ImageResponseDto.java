package com.adit.backend.domain.image.dto.response;

import com.adit.backend.domain.image.entity.Image;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO for {@link Image}
 */
@Builder
public record ImageResponseDto(@NotNull(message = "이미지 ID는 null일 수 없습니다.")
							   Long id,
							   @Nullable
							   Long placeId,
							   @Nullable
							   Long userPlaceId,
							   @Nullable
							   Long userEventId,
							   @Nullable
							   Long eventId,
							   @NotBlank(message = "이미지 주소는 공백일 수 없습니다.")
							   String url) {
}