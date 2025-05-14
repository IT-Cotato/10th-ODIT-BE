package com.adit.backend.domain.image.service;

import static com.adit.backend.domain.image.enums.Directory.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.image.entity.PlaceImage;
import com.adit.backend.domain.image.repository.PlaceImageRepository;
import com.adit.backend.domain.place.dto.request.PlaceRequestDto;
import com.adit.backend.domain.place.entity.Place;
import com.adit.backend.infra.s3.service.AwsS3Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImageService {

	private final AwsS3Service s3Service;
	private final PlaceImageRepository placeImageRepository;

	public void addNewPlaceImage(PlaceRequestDto request, Place place) {
		List<PlaceImage> imageList = s3Service.uploadFile(request.imageUrlList(), PLACE.getPath())
			.join()
			.stream()
			.map(url -> PlaceImage.builder().url(url).build())
			.toList();
		imageList.forEach(place::addImage);
		placeImageRepository.saveAll(imageList);
	}
}

