package com.adit.backend.domain.user.dto.request;

import com.adit.backend.domain.user.entity.Friendship;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Friendship}
 */
public record FriendRequestDto(@NotNull(message = "From User ID must not be null") Long fromUserId,
							   @NotNull(message = "To User ID must not be null") Long toUserId) {
}