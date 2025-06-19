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
public class PlaceConverter {
	public Place toEntity(PlaceRequestDto requestDto, Long placeId) {
		return Place.builder()
			.id(placeId)
			.placeName(requestDto.placeName())
			.addressName(requestDto.addressName())
			.latitude(requestDto.latitude())
			.longitude(requestDto.longitude())
			.roadAddressName(requestDto.roadAddressName())
			.subCategory(requestDto.subCategory())
			.url(requestDto.url())
			.build();

	}

	public PlaceResponseDto placeToResponse(Place place) {
		return PlaceResponseDto.builder()
			.placeId(place.getId())
			.placeName(place.getPlaceName())
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.subCategory(place.getSubCategory())
			.roadAddressName(place.getRoadAddressName())
			.addressName(place.getAddressName())
			.url(place.getUrl())
			.imageUrlList(Optional.ofNullable(place.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(PlaceImage::getUrl)
				.collect(Collectors.toList()))
			.build();
	}

	public PlaceResponseDto userPlaceToResponse(UserPlace userPlace) {
		return PlaceResponseDto.builder()
			.placeId(userPlace.getPlace().getId())
			.userPlaceId(userPlace.getId())
			.memo(userPlace.getMemo())
			.visited(userPlace.getVisited())
			.placeName(userPlace.getPlace().getPlaceName())
			.subCategory(userPlace.getPlace().getSubCategory())
			.roadAddressName(userPlace.getPlace().getRoadAddressName())
			.addressName(userPlace.getPlace().getAddressName())
			.imageUrlList(Optional.ofNullable(userPlace.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(UserPlaceImage::getUrl)
				.collect(Collectors.toList()))
			.longitude(userPlace.getPlace().getLongitude())
			.latitude(userPlace.getPlace().getLatitude())
			.build();
	}

	public PlaceResponseDto friendToResponse(UserPlace userPlace) {
		return PlaceResponseDto.builder()
			.placeId(userPlace.getPlace().getId())
			.userPlaceId(userPlace.getId())
			.memo(userPlace.getMemo())
			.visited(userPlace.getVisited())
			.placeName(userPlace.getPlace().getPlaceName())
			.subCategory(userPlace.getPlace().getSubCategory())
			.roadAddressName(userPlace.getPlace().getRoadAddressName())
			.addressName(userPlace.getPlace().getAddressName())
			.imageUrlList(Optional.ofNullable(userPlace.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(UserPlaceImage::getUrl)
				.collect(Collectors.toList()))
			.friendUserId(userPlace.getUser().getId())
			.profile(userPlace.getUser().getProfile())
			.build();
	}

}
