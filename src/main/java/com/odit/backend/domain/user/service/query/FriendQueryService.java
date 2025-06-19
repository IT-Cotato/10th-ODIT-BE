package com.odit.backend.domain.user.service.query;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.user.converter.UserConverter;
import com.odit.backend.domain.user.dto.response.FriendRequestResponseDto;
import com.odit.backend.domain.user.dto.response.UserResponse;
import com.odit.backend.domain.user.entity.Friendship;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.domain.user.exception.FriendShipException;
import com.odit.backend.domain.user.exception.UserException;
import com.odit.backend.domain.user.repository.FriendshipRepository;
import com.odit.backend.domain.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class FriendQueryService {

	private final UserRepository userRepository;
	private final FriendshipRepository friendshipRepository;
	private final UserConverter userConverter;

	/**
	 * 친구 요청 목록 확인
	 */
	public List<FriendRequestResponseDto> checkRequest(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));
		List<Friendship> sentFriendRequests = user.getSentFriendRequests();
		List<Friendship> receivedFriendRequests = user.getReceivedFriendRequests();

		Map<String, List<UserResponse.InfoDto>> allRequests = new HashMap<>();

		// 사용자의 보낸 요청
		allRequests.put("sentRequests", sentFriendRequests.stream()
			.map(friendship -> friendshipRepository.findByUser(friendship.getFromUser(), friendship.getToUser()))
			.filter(friendship -> !friendship.getStatus())
			.map(friendship -> userRepository.findById(friendship.getFromUser().getId())
				.orElseThrow(() -> new UserException(USER_NOT_FOUND)))
			.map(userConverter::InfoDto)
			.toList());

		// 사용자의 받은 요청
		allRequests.put("receivedRequests", receivedFriendRequests.stream()
			.map(friendship -> friendshipRepository.findByUser(friendship.getFromUser(), friendship.getToUser()))
			.filter(friendship -> !friendship.getStatus())
			.map(friendship -> userRepository.findById(friendship.getToUser().getId())
				.orElseThrow(() -> new UserException(USER_NOT_FOUND)))
			.map(userConverter::InfoDto)
			.toList());

		return allRequests.entrySet().stream()
			.map(entry -> new FriendRequestResponseDto(entry.getKey(), entry.getValue()))
			.toList();

	}

	/**
	 * 친구 목록 확인
	 */
	public List<UserResponse.InfoDto> findFriends(Long userId) {
		List<Long> friendsId = friendshipRepository.findFriends(userId);
		return friendsId.stream()
			.map(id -> userRepository.findById(id)
				.orElseThrow(() -> new UserException(USER_NOT_FOUND)))
			.map(userConverter::InfoDto)
			.toList();
	}

	/**
	 * 사용자 검색
	 */
	public List<FriendRequestResponseDto> findUser(String nickName, Long userId) {
		User searchedUser = userRepository.findByNickname(nickName)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		List<Long> friendsId = friendshipRepository.findFriends(userId);
		Friendship byUser = friendshipRepository.findByUser(user, searchedUser);

		Map<String, UserResponse.InfoDto> response = new HashMap<>();
		// nickName 으로 검색된 사용자가 친구 요청 대기중이거나 이미 친구라면 메시지만 반환
		if (friendsId.contains(searchedUser.getId()) || byUser != null) {
			response.put("Already processed friend", userConverter.InfoDto(searchedUser));
		}
		// 그렇지 않다면 검색된 사용자 정보 반환
		else {
			response.put("unprocessed friend", userConverter.InfoDto(searchedUser));
		}
		return response.entrySet().stream()
			.map(entry -> new FriendRequestResponseDto(entry.getKey(),
				Collections.singletonList(entry.getValue())))
			.toList();

	}

	public boolean isExistsFriendship(Long fromUserId, Long toUserId) {
		Friendship forward = getFriendshipOrDefault(fromUserId, toUserId);
		Friendship reverse = getFriendshipOrDefault(toUserId, fromUserId);

		boolean isForwardApproved = forward.getStatus();
		boolean isReverseApproved = reverse.getStatus();

		// A → B 요청이 이미 있으나, 상대방(B)은 수락하지 않은 경우: 중복 요청 금지
		if (isForwardApproved && !isReverseApproved) {
			throw new FriendShipException(ALREADY_REQUESTED);
		}

		// A가 요청을 보내지 않은 상태에서, B가 보낸 요청이 아직 수락되지 않은 경우
		if (!isForwardApproved && !isReverseApproved) {
			throw new FriendShipException(PENDING_REQUEST);
		}

		// 양측 모두 승인된 경우에만 친구 관계가 완성된 것으로 판단
		return isForwardApproved && isReverseApproved;
	}

	public Friendship getFriendshipOrDefault(Long requestor, Long responder) {
		return friendshipRepository.findByFromUser_IdAndToUser_Id(requestor, responder)
			.orElse(Friendship.builder().status(false).build());
	}

}
