package com.odit.backend.domain.place.service.command;

import org.springframework.stereotype.Service;

import com.odit.backend.domain.place.entity.Place;
import com.odit.backend.domain.place.entity.PlaceStatistics;
import com.odit.backend.domain.place.repository.PlaceStatisticsRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceStatisticsCommandService {

	private final PlaceStatisticsRepository placeStatisticsRepository;

	public void saveOrCount(Place place) {

		PlaceStatistics foundPlace = placeStatisticsRepository.findByPlaceId(place.getId())
			.orElseGet(() -> PlaceStatistics.builder()
				.place(place)
				.build());

		foundPlace.updateBookMarkCount();
		placeStatisticsRepository.save(foundPlace);
	}

}
