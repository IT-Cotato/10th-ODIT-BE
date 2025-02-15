package com.adit.backend.domain.place.service.command;

import org.springframework.stereotype.Service;

import com.adit.backend.domain.place.entity.CommonPlace;
import com.adit.backend.domain.place.entity.PlaceStatistics;
import com.adit.backend.domain.place.repository.PlaceStatisticsRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceStatisticsCommandService {

	private final PlaceStatisticsRepository placeStatisticsRepository;
	public void saveOrCount(CommonPlace commonPlace) {

		PlaceStatistics place = placeStatisticsRepository.findByCommonPlaceId(commonPlace.getId())
			.orElseGet(() -> PlaceStatistics.builder()
							.commonPlace(commonPlace)
				   			.build());

		place.updateBookMarkCount();
		placeStatisticsRepository.save(place);
	}


}
