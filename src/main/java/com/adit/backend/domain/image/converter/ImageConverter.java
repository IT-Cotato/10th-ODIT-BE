package com.adit.backend.domain.image.converter;

import org.springframework.stereotype.Component;

import com.adit.backend.domain.event.dto.response.UserEventImageResponseDto;
import com.adit.backend.domain.image.entity.UserEventImage;
import com.adit.backend.domain.image.entity.UserPlaceImage;
import com.adit.backend.domain.place.dto.response.UserPlaceImageResponseDto;

@Component
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
}