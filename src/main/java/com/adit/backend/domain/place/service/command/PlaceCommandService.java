package com.adit.backend.domain.place.service.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.image.service.PlaceImageService;
import com.adit.backend.domain.place.converter.PlaceConverter;
import com.adit.backend.domain.place.dto.request.PlaceRequestDto;
import com.adit.backend.domain.place.dto.response.PlaceResponseDto;
import com.adit.backend.domain.place.entity.Place;
import com.adit.backend.domain.place.repository.PlaceRepository;
import com.adit.backend.domain.place.service.query.PlaceQueryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceCommandService {

	private final PlaceRepository placeRepository;
	private final PlaceQueryService placeQueryService;
	private final PlaceConverter placeConverter;
	private final PlaceImageService placeImageService;

	// 카카오맵 url -> 기존 공통 장소 반환 or 새로운 공통 장소 생성
	public Place saveOrFindPlace(PlaceRequestDto request) {
		Long placeId = extractTrailingDigits(request.url());
		return placeRepository.findById(placeId).orElseGet(() -> {
			Place place = placeConverter.toEntity(request, placeId);
			if (!request.imageUrlList().isEmpty()) {
				placeImageService.addNewPlaceImage(request, place);
			}
			return placeRepository.save(place);
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
