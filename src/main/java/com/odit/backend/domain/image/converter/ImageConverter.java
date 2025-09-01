package com.odit.backend.domain.image.converter;

import com.odit.backend.domain.event.dto.response.UserEventImageResponseDto;
import com.odit.backend.domain.image.entity.UserEventImage;
import com.odit.backend.domain.image.entity.UserPlaceImage;
import com.odit.backend.domain.place.dto.response.UserPlaceImageResponseDto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageConverter {
	public UserPlaceImageResponseDto toResponse(UserPlaceImage userPlaceImage) {
		return UserPlaceImageResponseDto.builder()
			.userPlaceImageId(userPlaceImage.getId())
			.userPlaceId(userPlaceImage.getUserPlace().getId())
			.url(userPlaceImage.getUrl())
			.build();
	}

	public UserEventImageResponseDto toResponse(UserEventImage userEventImage) {
		return UserEventImageResponseDto.builder()
			.id(userEventImage.getId())
			.userEventId(userEventImage.getUserEvent().getId())
			.url(userEventImage.getUrl())
			.build();
	}

	public static UserEventImage toDefaultEntity(String url) {
		return UserEventImage.builder().url(url).build();
	}
}