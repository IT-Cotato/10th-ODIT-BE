package com.odit.backend.domain.user.dto.response;

import java.io.Serializable;

import com.odit.backend.domain.user.entity.Friendship;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO for {@link Friendship}
 */
@Builder
public record FriendshipResponseDto(Long id, @NotNull(message = "From User ID must not be nul") Long fromUserId,
									@NotNull(message = "To User ID must not be nul") Long toUserId, Boolean status)
	implements Serializable {

}