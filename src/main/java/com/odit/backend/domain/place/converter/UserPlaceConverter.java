package com.odit.backend.domain.place.converter;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.odit.backend.domain.image.entity.PlaceImage;
import com.odit.backend.domain.image.entity.UserPlaceImage;
import com.odit.backend.domain.place.dto.request.PlaceRequestDto;
import com.odit.backend.domain.place.dto.response.PlaceResponseDto;
import com.odit.backend.domain.place.entity.Place;
import com.odit.backend.domain.place.entity.UserPlace;

@Component
public class UserPlaceConverter {

	public UserPlace toEntity(PlaceRequestDto request) {
		return UserPlace.builder()
			.memo(request.memo())
			.visited(false)
			.build();
	}

	public UserPlace toEntity(Place place) {
		return UserPlace.builder()
			.visited(false)
			.build();
	}

	/**
	 * 일반 사용자용 응답 변환 메서드
	 */
	public PlaceResponseDto toResponse(UserPlace userPlace) {
		return PlaceResponseDto.builder()
			.placeId(userPlace.getPlace().getId())
			.userPlaceId(userPlace.getId())
			.memo(userPlace.getMemo())
			.visited(userPlace.getVisited())
			.placeName(userPlace.getPlace().getPlaceName())
			// Place 에서 latitude, longitude, url 등 필요한 필드가 존재한다고 가정
			.latitude(userPlace.getPlace().getLatitude())
			.longitude(userPlace.getPlace().getLongitude())
			.subCategory(userPlace.getPlace().getSubCategory())
			.roadAddressName(userPlace.getPlace().getRoadAddressName())
			.addressName(userPlace.getPlace().getAddressName())
			.url(userPlace.getPlace().getUrl())
			// Place에 연관된 모든 이미지의 url 목록 생성
			.imageUrlList(Optional.ofNullable(userPlace.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(UserPlaceImage::getUrl)
				.collect(Collectors.toList()))
			.build();
	}

	/**
	 * 친구용 응답 변환 메서드 (friendUserId, profile 정보 추가)
	 */
	public PlaceResponseDto toFriendResponse(UserPlace userPlace) {
		return PlaceResponseDto.builder()
			.placeId(userPlace.getPlace().getId())
			.userPlaceId(userPlace.getId())
			.memo(userPlace.getMemo())
			.visited(userPlace.getVisited())
			.placeName(userPlace.getPlace().getPlaceName())
			.latitude(userPlace.getPlace().getLatitude())
			.longitude(userPlace.getPlace().getLongitude())
			.subCategory(userPlace.getPlace().getSubCategory())
			.roadAddressName(userPlace.getPlace().getRoadAddressName())
			.addressName(userPlace.getPlace().getAddressName())
			.url(userPlace.getPlace().getUrl())
			.imageUrlList(userPlace.getPlace().getImages().stream()
				.map(PlaceImage::getUrl)
				.collect(Collectors.toList()))
			.friendUserId(userPlace.getUser().getId())
			.profile(userPlace.getUser().getProfile())
			.build();
	}
}
