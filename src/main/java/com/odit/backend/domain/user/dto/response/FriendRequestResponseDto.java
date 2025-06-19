package com.odit.backend.domain.user.dto.response;

import java.util.List;

public record FriendRequestResponseDto(String status, List<UserResponse.InfoDto> userList){

}
