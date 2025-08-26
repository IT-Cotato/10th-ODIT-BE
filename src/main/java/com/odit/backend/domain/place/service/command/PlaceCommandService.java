package com.odit.backend.domain.place.service.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.image.service.command.PlaceImageCommandService;
import com.odit.backend.domain.place.converter.PlaceConverter;
import com.odit.backend.domain.place.dto.request.PlaceRequestDto;
import com.odit.backend.domain.place.dto.response.PlaceResponseDto;
import com.odit.backend.domain.place.entity.Place;
import com.odit.backend.domain.place.repository.PlaceRepository;
import com.odit.backend.domain.place.service.query.PlaceQueryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceCommandService {

	private final PlaceRepository placeRepository;
	private final PlaceQueryService placeQueryService;
	private final PlaceConverter placeConverter;
	private final PlaceImageCommandService placeImageCommandService;

	// 카카오맵 url -> 기존 공통 장소 반환 or 새로운 공통 장소 생성
	public Place saveOrFindPlace(PlaceRequestDto request) {
		Long sequence = extractTrailingDigits(request.url());
		return placeRepository.findBySequence(sequence).orElseGet(() -> {
			Place place = placeConverter.toEntity(request, sequence);
			placeRepository.save(place);
			if (!request.imageUrlList().isEmpty()) {
				placeImageCommandService.addNewPlaceImage(request, place);
			}
			return place;
		});
	}

	public PlaceResponseDto updatePlace(Long placeId, PlaceRequestDto requestDto) {
		Place place = placeQueryService.getPlaceById(placeId);
		place.updatePlace(requestDto);
		return placeConverter.placeToResponse(place);
	}

	public long extractTrailingDigits(String url) {
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return Long.parseLong(matcher.group(1));
		}
		return 0;
	}

}
