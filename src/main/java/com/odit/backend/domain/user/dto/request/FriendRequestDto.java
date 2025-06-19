package com.odit.backend.domain.user.dto.request;

import com.odit.backend.domain.user.entity.Friendship;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Friendship}
 */
public record FriendRequestDto(@NotNull(message = "From User ID must not be null") Long fromUserId,
							   @NotNull(message = "To User ID must not be null") Long toUserId) {
}