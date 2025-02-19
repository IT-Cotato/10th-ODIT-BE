package com.adit.backend.domain.user.dto.response;

import java.util.List;

import com.adit.backend.domain.place.dto.response.PlaceResponseDto;

public record FriendRequestResponseDto(String status, List<UserResponse.InfoDto> userList){

}
