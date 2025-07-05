package com.odit.backend.domain.user.service.command;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.notification.service.NotificationGenerationService;
import com.odit.backend.domain.user.converter.FriendConverter;
import com.odit.backend.domain.user.dto.request.FriendRequestDto;
import com.odit.backend.domain.user.dto.response.FriendshipResponseDto;
import com.odit.backend.domain.user.entity.Friendship;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.domain.user.exception.FriendShipException;
import com.odit.backend.domain.user.exception.UserException;
import com.odit.backend.domain.user.repository.FriendshipRepository;
import com.odit.backend.domain.user.repository.UserRepository;
import com.odit.backend.domain.user.service.query.FriendQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class FriendCommandService {

	private final UserRepository userRepository;
	private final FriendshipRepository friendshipRepository;
	private final FriendConverter friendConverter;
	private final NotificationGenerationService notificationGenerationService;
	private final FriendQueryService friendQueryService;

	// 친구 요청 보내기
	public FriendshipResponseDto sendFriendRequest(FriendRequestDto requestDto) {
		if (friendQueryService.isExistsFriendship(requestDto.fromUserId(), requestDto.toUserId())) {
			throw new FriendShipException(FRIENDSHIP_ALREADY_EXISTS);
		}
		User fromUser = userRepository.findById(requestDto.fromUserId())
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));
		User toUser = userRepository.findById(requestDto.toUserId())
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		// 친구 요청(정방향)
		Friendship forwardRequest = friendConverter.toEntity(fromUser, toUser, true);
		// 친구 요청(역방향)
		Friendship reverseRequest = friendConverter.toEntity(toUser, fromUser, false);

		Friendship savedForwardRequest = friendshipRepository.save(forwardRequest);
		friendshipRepository.save(reverseRequest);
		notificationGenerationService.createNotificationOfAFriendRequest(savedForwardRequest);
		return friendConverter.toResponse(savedForwardRequest);
	}

	// 친구 요청 수락
	public void acceptFriendRequest(Long requestId) {
		Friendship friendRequest = friendshipRepository.findById(requestId)
			.orElseThrow(() -> new FriendShipException(FRIEND_REQUEST_NOT_FOUND));
		friendRequest.acceptRequest();
		notificationGenerationService.createNotificationOfAFriendAccept(friendRequest);
	}

	// 친구 요청 거절
	public void rejectFriendRequest(Long requestId) {
		Friendship friendRequest = friendshipRepository.findById(requestId)
			.orElseThrow(() -> new FriendShipException(FRIEND_REQUEST_NOT_FOUND));

		User fromUser = friendRequest.getFromUser();
		User toUser = friendRequest.getToUser();

		Friendship friendship = friendshipRepository.findByUser(fromUser, toUser);

		friendshipRepository.deleteById(requestId);
		friendshipRepository.delete(friendship);
	}

	// 친구 삭제
	public void removeFriend(Long userId, String NickName) {
		Long friendId = friendshipRepository.findFriendIdByUserIdAndNickname(userId, NickName)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		friendshipRepository.deleteFriend(userId, friendId);


	}
}