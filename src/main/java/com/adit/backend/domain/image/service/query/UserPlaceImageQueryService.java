package com.adit.backend.domain.image.service.query;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.image.entity.UserPlaceImage;
import com.adit.backend.domain.image.repository.UserPlaceImageRepository;
import com.adit.backend.domain.place.exception.PlaceException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPlaceImageQueryService {

	private final UserPlaceImageRepository userPlaceImageRepository;

	public UserPlaceImage getImageById(Long imageId) {
		return userPlaceImageRepository.findById(imageId)
			.orElseThrow(() -> new PlaceException(IMAGE_NOT_FOUND));
	}
}
