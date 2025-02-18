package com.adit.backend.domain.place.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.event.exception.EventException;
import com.adit.backend.domain.event.repository.UserEventRepository;
import com.adit.backend.domain.image.dto.request.ImageUpdateRequestDto;
import com.adit.backend.domain.image.dto.response.ImageResponseDto;
import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.domain.image.enums.Directory;
import com.adit.backend.domain.image.repository.ImageRepository;
import com.adit.backend.domain.image.service.command.ImageCommandService;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.domain.place.exception.PlaceException;
import com.adit.backend.domain.place.repository.UserPlaceRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImageService {

	private final ImageCommandService imageCommandService;
	private final UserPlaceRepository userPlaceRepository;
	private final ImageRepository imageRepository;

	/**
	 * 이벤트에 새로운 이미지 추가
	 */
	public List<ImageResponseDto> addEventImages(Long userPlaceId, List<MultipartFile> images) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));

		List<Image> uploadedImages = imageCommandService.uploadImages(images, Directory.PLACE.getPath());

		// 이미지 연관관계 설정
		uploadedImages.forEach(userPlace::addImage);
		userPlaceRepository.save(userPlace);

		return uploadedImages.stream()
			.map(imageCommandService::toResponse)
			.toList();

	}

	/**
	 * 기존 이벤트 이미지 업데이트 (기존 이미지 교체)
	 */
	public List<ImageResponseDto> updateEventImages(Long userPlaceId, List<ImageUpdateRequestDto> imageUpdateRequests) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));

		return imageUpdateRequests.stream()
			.map(req -> imageCommandService.updateImage(req.imageId(), req.newImage()))
			.toList();
	}

	/**
	 * 이벤트 이미지 삭제 (DB에서도 삭제)
	 */
	public void deleteEventImage(Long userPlaceId, Long imageId) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));

		//삭제할 이미지 찾기
		Image image = userPlace.getImages().stream()
			.filter(img -> img.getId().equals(imageId))
			.findFirst()
			.orElseThrow(() -> new PlaceException(IMAGE_NOT_FOUND));

		try {
			// 1. S3에서 이미지 삭제
			imageCommandService.deleteImage(image.getId());

			// 2. UserEvent에서 이미지 제거 (메모리 상 제거)
			userPlace.getImages().remove(image);

			// 3. DB에서 이미지 삭제
			imageRepository.delete(image);
			log.info("[이벤트 이미지 삭제 완료] userPlaceId = {}, imageId = {}", userPlaceId, imageId);

		} catch (Exception e) {
			log.error("[이벤트 이미지 삭제 실패] userPlaceId = {}, imageId = {}, 이유: {}", userPlaceId, imageId, e.getMessage(), e);
			throw new PlaceException(IMAGE_DELETE_FAILED);
		}
	}
}

