package com.adit.backend.domain.place.dto.response;

import java.util.List;

import com.adit.backend.domain.user.dto.response.UserResponse;

public record FriendPlaceResponseDto(PlaceResponseDto place, List<UserResponse.InfoDto> userList){

}
