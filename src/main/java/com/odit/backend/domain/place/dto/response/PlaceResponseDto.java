package com.odit.backend.domain.place.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "장소 응답")
public record PlaceResponseDto(


	@Schema(
		description = "place Id",
		example = "1"
	)
	Long placeId,

	@Schema(
		description = "userPlace Id",
		example = "1"
	)
	Long userPlaceId,

	@Schema(
		description = "사용자 메모"
	)
	String memo,

	@Schema(
		description = "해당 장소 방문여부",
		example = "true"
	)
	Boolean visited,

	@Schema(
		description = "장소 이름",
		example = "스타벅스"
	)
	String placeName,

	@Schema(
		description = "장소 위도",
		example = "37.5665"
	)
	BigDecimal latitude,

	@Schema(
		description = "장소 경도",
		example = "127.0458"
	)
	BigDecimal longitude,

	@Schema(
		description = "장소 카테고리",
		example = "음식점"
	)
	String subCategory,

	@Schema(
		description = "장소의 도로명 주소",
		example = "서울 강남구 영동대로 513"
	)
	String roadAddressName,

	@Schema(
		description = "장소의 지번 주소",
		example = "서울 강남구 삼성동 159"
	)
	String addressName,

	@Schema(
		description = "카카오 맵 url",
		example = "https://place.map.kakao.com/1382220123"
	)
	String url,

	@Schema(
		description = "장소 이미지 url 리스트"
	)
	List<String> imageUrlList,

	@Schema(
		description = "친구 Id",
		example = "1"
	)
	Long friendUserId,

	@Schema(
		description = "사용자의 profile",
		example = "http://k.kakaocdn.net/dn/c6s5g4/btsEtkScM1S/2496XzpgkN4SW7HKFvvcT0/img_640x640.jpg"
	)
	String profile
) {
}
