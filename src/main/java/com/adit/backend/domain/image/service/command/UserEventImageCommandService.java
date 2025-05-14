package com.adit.backend.domain.image.service.command;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.adit.backend.domain.event.dto.response.UserEventImageResponseDto;
import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.event.exception.EventException;
import com.adit.backend.domain.event.repository.UserEventRepository;
import com.adit.backend.domain.image.dto.request.ImageUpdateRequestDto;
import com.adit.backend.domain.image.entity.UserEventImage;
import com.adit.backend.domain.image.enums.Directory;
import com.adit.backend.domain.image.repository.UserEventImageRepository;
import com.adit.backend.domain.image.service.query.UserEventImageQueryService;
import com.adit.backend.infra.s3.service.AwsS3Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserEventImageCommandService {
	private final UserEventImageRepository userEventImageRepository;
	private final AwsS3Service s3Service;
	private final UserEventRepository userEventRepository;
	private final UserEventImageQueryService userEventImageQueryService;

	/**
	 * 이벤트에 새로운 이미지 추가
	 */
	public List<UserEventImageResponseDto> addUserEventImages(Long eventId, List<MultipartFile> images) {
		UserEvent userEvent = userEventRepository.findById(eventId)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

		List<UserEventImage> uploadedImages = uploadImages(images, Directory.EVENT.getPath());

		// 이미지 연관관계 설정
		uploadedImages.forEach(userEvent::addImage);
		userEventRepository.save(userEvent);

		return uploadedImages.stream()
			.map(UserEventImageResponseDto::of)
			.toList();
	}

	public List<UserEventImage> uploadImages(List<MultipartFile> files, String dirName) {
		List<UserEventImage> imageList = s3Service.uploadFiles(files, dirName).join()
			.stream()
			.map(url -> UserEventImage.builder().url(url).build())
			.toList();
		userEventImageRepository.saveAll(imageList);
		return imageList;
	}

	/**
	 * 기존 이벤트 이미지 업데이트 (기존 이미지 교체)
	 */
	public List<UserEventImageResponseDto> updateUserEventImages(Long eventId,
		List<ImageUpdateRequestDto> imageUpdateRequests) {
		UserEvent userEvent = userEventRepository.findById(eventId)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

		return imageUpdateRequests.stream()
			.map(req -> updateUserEventImage(req.imageId(), req.newImage()))
			.toList();
	}

	private UserEventImageResponseDto updateUserEventImage(Long imageId, MultipartFile multipartFile) {
		UserEventImage image = userEventImageQueryService.getImageById(imageId);
		String newImageUrl = s3Service.updateImage(image.getUrl(), multipartFile).join();
		image.updateUrl(newImageUrl);
		return UserEventImageResponseDto.of(image);
	}

	/**
	 * 이벤트 이미지 삭제 (DB에서도 삭제)
	 */
	public void deleteEventImage(Long eventId, Long imageId) {
		UserEvent userEvent = userEventRepository.findById(eventId)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

		//삭제할 이미지 찾기
		UserEventImage image = userEvent.getImages().stream()
			.filter(img -> img.getId().equals(imageId))
			.findFirst()
			.orElseThrow(() -> new EventException(IMAGE_NOT_FOUND));

		try {
			// 1. S3에서 이미지 삭제
			deleteImage(image.getId());

			// 2. UserEvent에서 이미지 제거 (메모리 상 제거)
			userEvent.getImages().remove(image);

			// 3. DB에서 이미지 삭제
			userEventImageRepository.delete(image);
			log.info("[이벤트 이미지 삭제 완료] eventId = {}, imageId = {}", eventId, imageId);

		} catch (Exception e) {
			log.error("[이벤트 이미지 삭제 실패] eventId = {}, imageId = {}, 이유: {}", eventId, imageId, e.getMessage(), e);
			throw new EventException(IMAGE_DELETE_FAILED);
		}
	}

	public void deleteImage(Long id) {
		userEventImageRepository.deleteById(id);
	}
}
