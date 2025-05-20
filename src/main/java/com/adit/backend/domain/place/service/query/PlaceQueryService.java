package com.adit.backend.domain.place.service.query;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.place.converter.PlaceConverter;
import com.adit.backend.domain.place.dto.response.PlaceResponseDto;
import com.adit.backend.domain.place.entity.Place;
import com.adit.backend.domain.place.exception.PlaceException;
import com.adit.backend.domain.place.repository.PlaceRepository;
import com.adit.backend.domain.place.repository.UserPlaceRepository;
import com.adit.backend.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceQueryService {

	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 5;
	private final PlaceRepository placeRepository;
	private final PlaceConverter placeConverter;
	private final UserPlaceRepository userPlaceRepository;

	public Place getPlaceById(Long id) {
		return placeRepository.findById(id)
			.orElseThrow(() -> new PlaceException(PLACE_NOT_FOUND));
	}

	//인기 기반으로 장소 찾기
	public List<PlaceResponseDto> getPlaceByPopular() {
		//PlaceStatistics 엔티티에서 1위부터 5위까지의 placeId를 가져옴
		Pageable pageable = PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
		List<Long> placesId = placeRepository.findByPopular(pageable);
		List<Place> places = placesId.stream()
			.map(this::getPlaceById).toList();
		return places.stream().map(placeConverter::placeToResponse).toList();
	}

	//특정 장소 상세정보 찾기
	public PlaceResponseDto getDetailedPlace(String placeName) {
		if (placeName.isBlank()) {
			throw new BusinessException(NOT_VALID_ERROR);
		}
		Place place = placeRepository.findByBusinessName(placeName)
			.orElseThrow(() -> new PlaceException(PLACE_NOT_FOUND));
		return placeConverter.placeToResponse(place);

	}

	public Map<Place, Integer> countPlacesByFriends(List<Long> friendsId) {
		Map<Place, Integer> friendsPlace = new HashMap<>();
		friendsId.forEach(id -> {
			userPlaceRepository.findByUserId(id)
				.forEach(userPlace -> friendsPlace.merge(userPlace.getPlace(), 1, Integer::sum));
		});
		return friendsPlace;
	}

	public List<Place> sortPlacesByFrequency(Map<Place, Integer> friendsPlace) {
		return friendsPlace.keySet().stream()
			.sorted((a1, b1) -> friendsPlace.get(b1) - friendsPlace.get(a1))
			.toList();
	}
}
