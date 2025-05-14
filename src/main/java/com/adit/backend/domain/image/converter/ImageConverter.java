package com.adit.backend.domain.image.converter;

import org.springframework.stereotype.Component;

import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.domain.place.dto.request.PlaceRequestDto;
import com.adit.backend.domain.place.dto.response.UserPlaceImageResponseDto;

@Component
public class ImageConverter {
	public Image toEntity(PlaceRequestDto request) {
		return Image.builder()
			.url(request.url())
			.build();
	}

	public UserPlaceImageResponseDto toResponse(Image image) {
		return UserPlaceImageResponseDto.builder()
			.userPlaceImageId(image.getId())
			.userPlaceId(image.getUserPlace().getId())
			.url(image.getUrl())
			.build();
	}

	public UserPlaceImageResponseDto toResponseForUserPlace(UserPlaceImageResponseDto imageResponseDto) {
		return UserPlaceImageResponseDto.builder()
			.userPlaceImageId(imageResponseDto.userPlaceImageId())
			.userPlaceId(imageResponseDto.userPlaceId())
			.url(imageResponseDto.url())
			.build();
	}

}