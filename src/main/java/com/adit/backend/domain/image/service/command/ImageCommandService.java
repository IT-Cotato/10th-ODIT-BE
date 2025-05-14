/*
package com.adit.backend.domain.image.service.command;

import static com.adit.backend.domain.image.enums.Directory.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.adit.backend.domain.event.dto.request.EventRequestDto;
import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.image.converter.ImageConverter;
import com.adit.backend.domain.image.dto.response.ImageResponseDto;
import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.domain.image.enums.Directory;
import com.adit.backend.domain.image.repository.ImageRepository;
import com.adit.backend.domain.image.repository.PlaceImageRepository;
import com.adit.backend.domain.image.service.query.ImageQueryService;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.infra.s3.service.AwsS3Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageCommandService {
	private final ImageRepository imageRepository;
	private final ImageConverter imageConverter;
	private final AwsS3Service s3Service;
	private final ImageQueryService imageQueryService;
	private final PlaceImageRepository placeImageRepository;

	public String uploadImage(String url) {
		return s3Service.uploadFile(List.of(url), TEST.getPath()).join().toString();
	}

	public ImageResponseDto updateImage(Long imageId, MultipartFile multipartFile) {
		Image image = imageQueryService.getImageById(imageId);
		String newImageUrl = s3Service.updateImage(image.getUrl(), multipartFile).join();
		image.updateUrl(newImageUrl);
		return imageConverter.toResponse(image); // ImageResponseDto 반환
	}

	*/
/**
	 * 여러 개의 MultipartFile을 업로드하는 메서드 추가
	 *//*

	public List<Image> uploadImages(List<MultipartFile> files, String dirName) {
		List<Image> imageList = s3Service.uploadFiles(files, dirName).join();
		imageRepository.saveAll(imageList);
		return imageList;
	}

	// 이미지 삭제
	public void deleteImage(Long imageId) {
		Image image = imageQueryService.getImageById(imageId);
		s3Service.deleteFile(image.getUrl());
		imageRepository.delete(image);
	}
	// Event에 이미지 연관관계 추가 후 저장
	public void addImageToEvent(EventRequestDto request, Event event) {
		List<Image> imageList = s3Service.uploadFile(request.imageUrlList(), EVENT.getPath()).join();
		imageList.forEach(event::addImage);
		imageRepository.saveAll(imageList);

	}

	// UserEvent에 이미지 연관관계 추가 후 저장
	public void addImageToUserEvent(EventRequestDto request, User user, UserEvent userEvent) {
		List<Image> imageList = s3Service.uploadFile(request.imageUrlList(), Directory.USER.getPath() + user.getId())
			.join();
		imageList.forEach(userEvent::addImage);
		imageRepository.saveAll(imageList);

	}

	// toResponse 메서드 추가
	public ImageResponseDto toResponse(Image image) {
		return new ImageResponseDto(
			image.getId(),
			image.getPlace(),
			image.getUserPlace(),
			image.getUserEvent(),
			image.getEvent(),
			image.getUrl()
		);
	}
}
*/
