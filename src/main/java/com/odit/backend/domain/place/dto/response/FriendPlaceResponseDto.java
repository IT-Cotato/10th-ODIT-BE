package com.odit.backend.domain.place.dto.response;

import java.util.List;

import com.odit.backend.domain.user.dto.response.UserResponse;

public record FriendPlaceResponseDto(PlaceResponseDto place,
									 List<UserResponse.InfoDto> userList){

}
