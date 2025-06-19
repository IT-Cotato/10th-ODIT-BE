package com.odit.backend.domain.user.converter;

import org.springframework.stereotype.Component;

import com.odit.backend.domain.user.dto.response.UserResponse;
import com.odit.backend.domain.user.entity.User;

@Component
public class UserConverter {
	public UserResponse.InfoDto InfoDto(User user) {
		return UserResponse.InfoDto.builder()
			.Id(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.name(user.getName())
			.role(user.getRole())
			.profile(user.getProfile())
			.build();
	}
}
