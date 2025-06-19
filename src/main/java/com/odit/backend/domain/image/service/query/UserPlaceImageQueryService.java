package com.odit.backend.domain.image.service.query;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.image.entity.UserPlaceImage;
import com.odit.backend.domain.image.repository.UserPlaceImageRepository;
import com.odit.backend.domain.place.exception.PlaceException;

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
