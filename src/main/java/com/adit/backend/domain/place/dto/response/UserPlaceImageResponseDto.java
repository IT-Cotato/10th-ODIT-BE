package com.adit.backend.domain.place.dto.response;

import com.adit.backend.domain.image.entity.UserPlaceImage;

import lombok.Builder;

@Builder
public record UserPlaceImageResponseDto(
	Long userPlaceImageId,
	Long userPlaceId,
	String url
) {

	public static UserPlaceImageResponseDto of(UserPlaceImage userPlaceImage) {
		return UserPlaceImageResponseDto.builder()
			.userPlaceImageId(userPlaceImage.getId())
			.userPlaceId(userPlaceImage.getUserPlace().getId())
			.url(userPlaceImage.getUrl())
			.build();
	}
}
