package com.adit.backend.domain.image.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.event.exception.EventException;
import com.adit.backend.domain.image.entity.UserEventImage;
import com.adit.backend.domain.image.repository.UserEventImageRepository;
import com.adit.backend.global.error.GlobalErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEventImageQueryService {

	private final UserEventImageRepository userEventImageRepository;

	public UserEventImage getImageById(Long imageId) {
		return userEventImageRepository.findById(imageId)
			.orElseThrow(() -> new EventException(GlobalErrorCode.IMAGE_NOT_FOUND));
	}
}
