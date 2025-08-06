package com.odit.backend.domain.place.dto.response;

import java.util.List;

import com.odit.backend.domain.user.dto.response.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 장소 응답")
public record FriendPlaceResponseDto(

	@Schema(
		description = "place에 대한 정보",
		example = "placeResponseDto 를 참고"
	)
	PlaceResponseDto place,


	@Schema(
		description = "해당 장소를 저장한 친구의 정보",
		example = "ID : 1"
			+ "email : hello@kakao.com"
		    + "name : 전규진"
		    + "nickname : 규진"
			+ "role : Quest"
			+ "profile : http://k.kakaocdn.net/dn/c6s5g4/btsEtkScM1S/2496XzpgkN4SW7HKFvvcT0/img_640x640.jpg"
	)
	List<UserResponse.InfoDto> userList){

}
