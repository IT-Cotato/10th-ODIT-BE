package com.adit.backend.domain.user.converter;

import org.springframework.stereotype.Component;

import com.adit.backend.domain.user.dto.response.FriendshipResponseDto;
import com.adit.backend.domain.user.entity.Friendship;
import com.adit.backend.domain.user.entity.User;

@Component
public class FriendConverter {

	public FriendshipResponseDto toResponse(Friendship friendship) {
		return FriendshipResponseDto.builder()
			.fromUserId(friendship.getFromUser().getId())
			.toUserId(friendship.getToUser().getId())
			.status(friendship.getStatus())
			.build();
	}

	public Friendship toEntity(User FromUser, User toUser, Boolean status) {
		return Friendship.builder()
			.fromUser(FromUser)
			.toUser(toUser)
			.status(status)
			.build();
	}

}
