package com.odit.backend.domain.place.service.command;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.image.service.command.UserPlaceImageCommandService;
import com.odit.backend.domain.notification.service.NotificationGenerationService;
import com.odit.backend.domain.place.converter.PlaceConverter;
import com.odit.backend.domain.place.converter.UserPlaceConverter;
import com.odit.backend.domain.place.dto.request.PlaceRequestDto;
import com.odit.backend.domain.place.dto.response.PlaceResponseDto;
import com.odit.backend.domain.place.entity.Place;
import com.odit.backend.domain.place.entity.UserPlace;
import com.odit.backend.domain.place.exception.PlaceException;
import com.odit.backend.domain.place.repository.PlaceRepository;
import com.odit.backend.domain.place.repository.UserPlaceRepository;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.domain.user.exception.UserException;
import com.odit.backend.domain.user.repository.UserRepository;
import com.odit.backend.domain.user.service.query.UserQueryService;
import com.odit.backend.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserPlaceCommandService {

	private final UserPlaceRepository userPlaceRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;
	private final UserQueryService userQueryService;
	private final PlaceCommandService placeCommandService;
	private final PlaceStatisticsCommandService placeStatisticsCommandService;
	private final NotificationGenerationService notificationGenerationService;

	private final PlaceConverter placeConverter;
	private final UserPlaceConverter userPlaceConverter;
	private final UserPlaceImageCommandService userPlaceImageCommandService;

	// 장소 저장시, EventStatistics.bookmarkCount 증가
	public PlaceResponseDto createUserPlace(Long userId, PlaceRequestDto request) {
		//장소 중복 검사
		duplicatePlace(userId, request.url());
		User user = userQueryService.findUserById(userId);
		Place place = placeCommandService.saveOrFindPlace(request);
		UserPlace userPlace = userPlaceConverter.toEntity(request);
		saveUserPlaceRelation(user, place, userPlace);
		if (!request.imageUrlList().isEmpty()) {
			userPlaceImageCommandService.addNewUserPlaceImage(request, user, userPlace);
		}
		placeStatisticsCommandService.saveOrCount(place);
		return placeConverter.userPlaceToResponse(userPlace);
	}

	// 장소 삭제
	public void deletePlace(Long userPlaceId) {
		if (!userPlaceRepository.existsById(userPlaceId)) {
			throw new PlaceException(USER_PLACE_NOT_FOUND);
		}
		userPlaceRepository.deleteById(userPlaceId);
	}

	//장소 메모 수정
	public PlaceResponseDto updateUserPlace(Long userPlaceId, String memo) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));
		if (memo.isBlank()) {
			throw new BusinessException(NOT_VALID_ERROR);
		}
		userPlace.updatedMemo(memo);
		return placeConverter.userPlaceToResponse(userPlace);
	}

	//장소 방문 여부 표시
	public void checkVisitedPlace(Long userPlaceId) {
		UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));
		userPlace.updatedVisited();
	}

	// user, place와 UserPlace 사이의 연관관계 설정 및 저장
	private void saveUserPlaceRelation(User user, Place place, UserPlace userPlace) {
		user.addUserPlace(userPlace);
		place.addUserPlace(userPlace);
		userPlaceRepository.save(userPlace);
		notificationGenerationService.createNotificationOfASavedPlace(user, place, userPlace);
	}

	public void duplicatePlace(Long userId, String requestUrl) {
		if (userPlaceRepository.findDuplicatePlace(userId, requestUrl) != null) {
			throw new PlaceException(USER_PLACE_DUPLICATE);
		}
	}

	// public PlaceResponseDto updateUserPlaceImage(Long userPlaceId, List<MultipartFile> newImageList) {
	// 	UserPlace userPlace = userPlaceRepository.findById(userPlaceId)
	// 		.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));
	//
	// 	List<Image> existingImages = userPlace.getImages();
	// 	if (newImageList == null || newImageList.isEmpty()) {
	// 		return userPlaceConverter.toResponse(userPlace);
	// 	}
	//
	// 	// 기존 이미지 업데이트 (stream() 활용)
	// 	List<String> updatedImageUrls = IntStream.range(0, Math.min(existingImages.size(), newImageList.size()))
	// 		.mapToObj(i -> {
	// 			Image oldImage = existingImages.get(i);
	// 			return imageCommandService.updateImage(oldImage.getId(), newImageList.get(i)).url();
	// 		})
	// 		.toList(); // 변경된 URL 리스트 저장
	//
	// 	// 업데이트된 URL을 한 번에 반영
	// 	IntStream.range(0, updatedImageUrls.size())
	// 		.forEach(i -> existingImages.get(i).updateUrl(updatedImageUrls.get(i)));
	//
	// 	// 새로운 이미지 추가 (Directory.EVENT.getPath() 사용)
	// 	if (newImageList.size() > existingImages.size()) {
	// 		List<MultipartFile> extraFiles = newImageList.subList(existingImages.size(), newImageList.size());
	//
	// 		if (!extraFiles.isEmpty()) {
	// 			List<Image> extraImages = imageCommandService.uploadImages(extraFiles, Directory.EVENT.getPath());
	// 			extraImages.forEach(userPlace::addImage);
	// 		}
	// 	}
	//
	// 	return userPlaceConverter.toResponse(userPlace);
	// }

	public PlaceResponseDto savedPlace(Long placeId, Long userId) {
		Place place = placeRepository.findById(placeId).orElseThrow(() -> new PlaceException(PLACE_NOT_FOUND));
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));
		duplicatePlace(userId, place.getUrl());
		UserPlace userPlace = userPlaceConverter.toEntity(place);
		saveUserPlaceRelation(user, place, userPlace);
		placeStatisticsCommandService.saveOrCount(place);
		if (!place.getImages().isEmpty()) {
			userPlaceImageCommandService.addBookMarkPlaceImage(place, user, userPlace);
		}

		return placeConverter.userPlaceToResponse(userPlace);
	}
}
