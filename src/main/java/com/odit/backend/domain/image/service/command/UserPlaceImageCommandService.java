package com.odit.backend.domain.image.service.command;

import static com.odit.backend.domain.image.enums.Directory.*;
import static com.odit.backend.global.error.GlobalErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.odit.backend.domain.image.converter.ImageConverter;
import com.odit.backend.domain.image.dto.request.ImageUpdateRequestDto;
import com.odit.backend.domain.image.entity.PlaceImage;
import com.odit.backend.domain.image.entity.UserPlaceImage;
import com.odit.backend.domain.image.repository.UserPlaceImageRepository;
import com.odit.backend.domain.image.service.query.UserPlaceImageQueryService;
import com.odit.backend.domain.place.dto.request.PlaceRequestDto;
import com.odit.backend.domain.place.dto.response.UserPlaceImageResponseDto;
import com.odit.backend.domain.place.entity.Place;
import com.odit.backend.domain.place.entity.UserPlace;
import com.odit.backend.domain.place.exception.PlaceException;
import com.odit.backend.domain.place.repository.UserPlaceRepository;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.infra.s3.service.AwsS3Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPlaceImageCommandService {

	private final UserPlaceRepository userPlaceRepository;
	private final AwsS3Service s3Service;
	private final UserPlaceImageRepository userPlaceImageRepository;
	private final UserPlaceImageQueryService userPlaceImageQueryService;
	private final ImageConverter imageConverter;

	public void addNewUserPlaceImage(PlaceRequestDto request, User user, UserPlace userPlace) {
		List<UserPlaceImage> imageList = s3Service.uploadFile(request.imageUrlList(), USER.getPath() + user.getId())
			.join()
			.stream()
			.map(url -> UserPlaceImage.builder().url(url).build())
			.toList();
		imageList.forEach(userPlace::addImage);
		userPlaceImageRepository.saveAll(imageList);
	}

	public void addBookMarkPlaceImage(Place place, User user, UserPlace userPlace) {
		List<UserPlaceImage> imageList = s3Service.uploadFile(
				place.getImages()
					.stream()

					.map(PlaceImage::getUrl).toList(), USER.getPath() + user.getId())
			.join()
			.stream()
			.map(url -> UserPlaceImage.builder().url(url).build())
			.toList();
		imageList.forEach(userPlace::addImage);
		userPlaceImageRepository.saveAll(imageList);
	}

	public List<UserPlaceImageResponseDto> addUserPlaceImages(Long userPlaceId, List<MultipartFile> images) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));

		List<UserPlaceImage> uploadedImages =
			s3Service.uploadFiles(images, USER.getPath() + userPlaceId)
				.join()
				.stream()
				.map(url -> UserPlaceImage.builder().url(url).build())
				.toList();

		userPlaceImageRepository.saveAll(uploadedImages);
		uploadedImages.forEach(userPlace::addImage);
		userPlaceRepository.save(userPlace);

		return uploadedImages.stream()
			.map(imageConverter::toResponse)
			.toList();
	}

	/**
	 * 기존 장소 이미지 업데이트 (기존 이미지 교체)
	 */
	public List<UserPlaceImageResponseDto> updatePlaceImages(List<ImageUpdateRequestDto> imageUpdateRequests) {
		return imageUpdateRequests.stream()
			.map(req -> updateUserPlaceImage(req.imageId(), req.newImage()))
			.toList();
	}

	private UserPlaceImageResponseDto updateUserPlaceImage(Long imageId, MultipartFile multipartFile) {
		UserPlaceImage image = userPlaceImageQueryService.getImageById(imageId);
		String newImageUrl = s3Service.updateImage(image.getUrl(), multipartFile).join();
		image.updateUrl(newImageUrl);
		return imageConverter.toResponse(image);
	}

	/**
	 * 장소 이미지 삭제 (DB에서도 삭제)
	 */
	public void deletePlaceImage(Long userPlaceId, Long imageId) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));

		//삭제할 이미지 찾기
		UserPlaceImage image = userPlace.getImages().stream()
			.filter(img -> img.getId().equals(imageId))
			.findFirst()
			.orElseThrow(() -> new PlaceException(IMAGE_NOT_FOUND));

		try {
			// 1. S3에서 이미지 삭제
			s3Service.deleteFile(image.getUrl());
			// 2. UserPlace 에서 이미지 제거 (메모리 상 제거)
			userPlace.getImages().remove(image);
			// 3. DB에서 이미지 삭제
			userPlaceImageRepository.delete(image);
			log.info("[장소 이미지 삭제 완료] userPlaceId = {}, imageId = {}", userPlaceId, imageId);

		} catch (Exception e) {
			log.error("[장소 이미지 삭제 실패] userPlaceId = {}, imageId = {}, 이유: {}", userPlaceId, imageId, e.getMessage(), e);
			throw new PlaceException(IMAGE_DELETE_FAILED);
		}
	}
}
