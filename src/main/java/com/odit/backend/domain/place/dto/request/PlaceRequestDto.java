package com.odit.backend.domain.place.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.odit.backend.domain.place.entity.Place;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Place}
 */
public record PlaceRequestDto(
	@Schema(
		description = "링크 추출 후 얻은 장소명",
		example = "스타벅스"
	)
	@NotBlank(message = "장소명은 필수 입력값입니다.")
	String placeName,
	@Schema(
		description = "해당 장소의 위도",
		example = "37.5665"
	)
	@NotNull(message = "위도는 필수 입력값입니다.")
	BigDecimal latitude,

	@Schema(
		description = "해당 장소의 경도",
		example = "127.0458"
	)
	@NotNull(message = "경도는 필수 입력값입니다.")
	BigDecimal longitude,

	@Schema(
		description = "해당 장소의 지번 주소",
		example = "서울 강남구 삼성동 159"
	)
	@NotBlank(message = "지번 주소는 필수 입력값입니다.")
	String addressName,

	@Schema(
		description = "해당 장소의 도로명 주소",
		example = "서울 강남구 영동대로 513"
	)
	@NotBlank(message = "도로명 주소는 필수 입력값입니다.")
	String roadAddressName,

	@Schema(
		description = "해당 장소가 속한 카테고리",
		example = "음식점"
	)
	@NotBlank(message = "카테고리는 필수 입력값입니다.")
	String subCategory,

	@Schema(
		description = "문화정보 API에서 가져온 ",
		example = "2432452"
	)
	@NotBlank(message = "링크는 필수 입력값입니다.")
	String url,

	@Schema(
		description = "카카오 맵 url",
		example = "https://place.map.kakao.com/1382220123"
	)
	String memo,

	@Schema(
		description = "해당 장소의 이미지",
		example = "이미지 url"
	)
	List<String> imageUrlList)
	implements Serializable {
}