package com.adit.backend.domain.place.service.query;

import static com.adit.backend.global.error.GlobalErrorCode.*;
import static com.adit.backend.global.util.MapUtil.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.place.converter.PlaceConverter;
import com.adit.backend.domain.place.dto.response.PlaceResponseDto;
import com.adit.backend.domain.place.entity.Place;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.domain.place.exception.PlaceException;
import com.adit.backend.domain.place.repository.UserPlaceRepository;
import com.adit.backend.domain.user.dto.response.UserResponse;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.domain.user.repository.FriendshipRepository;
import com.adit.backend.domain.user.service.query.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserPlaceQueryService {

	private final UserPlaceRepository userPlaceRepository;
	private final FriendshipRepository friendshipRepository;
	private final PlaceConverter placeConverter;
	private final PlaceQueryService placeQueryService;
	private final UserQueryService userQueryService;

	public List<PlaceResponseDto> getPlaceByCategory(List<String> subCategory, Long userId) {
		if (subCategory.stream().anyMatch(String::isBlank)) {
			throw new PlaceException(NOT_VALID);
		}

		// 중복 제거를 위한 Set
		Set<UserPlace> userPlaceSet = new HashSet<>();
		subCategory.forEach(partialCategory -> {
			List<UserPlace> foundPlaces = userPlaceRepository.findByCategory(partialCategory, userId);
			userPlaceSet.addAll(foundPlaces);
		});
		List<UserPlace> userPlaces = new ArrayList<>(userPlaceSet);
		if (userPlaces.isEmpty()) {
			throw new PlaceException(USER_PLACE_NOT_FOUND);
		}

		return userPlaces.stream().map(placeConverter::userPlaceToResponse).toList();
	}

	public List<PlaceResponseDto> getSavedPlace(Long userId) {
		List<UserPlace> userPlaces = userPlaceRepository.findByUserId(userId);
		if (userPlaces.isEmpty()) {
			throw new PlaceException(USER_PLACE_NOT_FOUND);
		}
		return userPlaces.stream().map(placeConverter::userPlaceToResponse).toList();
	}

	public List<PlaceResponseDto> getPlaceByLocation(double userLatitude, double userLongitude, Long userId) {
		List<UserPlace> userPlaces = userPlaceRepository.findByUserId(userId);
		if (userPlaces.isEmpty()) {
			throw new PlaceException(USER_PLACE_NOT_FOUND);
		}
		if (userPlaces.size() == 1) {
			return userPlaces.stream().map(placeConverter::userPlaceToResponse).toList();
		}
		// 저장한 장소가 2개 이상일 때 정렬
		List<UserPlace> placeByLocation = userPlaces.stream().sorted((place1, place2) -> {
			double distance1 = getDistance(place1.getPlace().getLatitude().doubleValue(),
				place1.getPlace().getLongitude().doubleValue(), userLatitude, userLongitude);
			double distance2 = getDistance(place2.getPlace().getLatitude().doubleValue(),
				place2.getPlace().getLongitude().doubleValue(), userLatitude, userLongitude);
			return Double.compare(distance1, distance2);
		}).toList();
		return placeByLocation.stream().map(placeConverter::userPlaceToResponse).toList();
	}

	public List<PlaceResponseDto> getPlaceByAddress(List<String> address, Long userId) {
		if (address.stream().anyMatch(String::isBlank)) {
			throw new PlaceException(NOT_VALID);
		}
		Set<UserPlace> userPlaceSet = new HashSet<>();
		address.forEach(partialAddress -> {
			List<UserPlace> foundPlaces = userPlaceRepository.findByAddress(partialAddress, userId);
			userPlaceSet.addAll(foundPlaces);
		});
		List<UserPlace> userPlaces = new ArrayList<>(userPlaceSet);
		if (userPlaces.isEmpty()) {
			throw new PlaceException(USER_PLACE_NOT_FOUND);
		}
		return userPlaces.stream().map(placeConverter::userPlaceToResponse).toList();
	}

	public Map<PlaceResponseDto, List<UserResponse.InfoDto>> getPlaceByFriend(Long userId) {
		Map<PlaceResponseDto, List<UserResponse.InfoDto>> response = new LinkedHashMap<>();
		List<Long> friendsId = friendshipRepository.findFriends(userId);
		if (friendsId.isEmpty()) {
			throw new PlaceException(FRIEND_NOT_FOUND);
		}
		Map<Place, Integer> friendsPlace = placeQueryService.countPlacesByFriends(friendsId);

		List<Place> sortedPlaces = placeQueryService.sortPlacesByFrequency(friendsPlace);

		sortedPlaces.forEach(place -> {
			List<UserResponse.InfoDto> friendInfoList = userQueryService.findUsersByPlaceId(place.getId());
			response.put(placeConverter.placeToResponse(place), friendInfoList);
		});
		return response;
	}

	public List<UserPlace> findRelatedUserPlace(User user, Place place) {
		return userPlaceRepository.findAllFriendsUserPlace(user.getId(), place.getId())
			.orElseThrow(() -> new PlaceException(USER_PLACE_NOT_FOUND));
	}
}
