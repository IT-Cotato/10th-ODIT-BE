package com.adit.backend.domain.place.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceResponseDto(
	@NotNull(message = "장소 아이디는 필수 입력값입니다.")
	Long placeId,

	@NotNull(message = "사용자 장소 아이디는 필수 입력값입니다.")
	Long userPlaceId,

	@NotBlank(message = "메모는 빈 값일 수 없습니다.")
	String memo,

	@NotNull(message = "방문 여부는 필수 입력값입니다.")
	Boolean visited,

	@NotBlank(message = "장소명은 빈 값일 수 없습니다.")
	String placeName,

	@NotNull(message = "위도는 필수 입력값입니다.")
	BigDecimal latitude,

	@NotNull(message = "경도는 필수 입력값입니다.")
	BigDecimal longitude,

	@NotBlank(message = "카테고리는 빈 값일 수 없습니다.")
	String subCategory,

	@NotBlank(message = "도로명 주소는 빈 값일 수 없습니다.")
	String roadAddressName,

	@NotBlank(message = "지번 주소는 빈 값일 수 없습니다.")
	String addressName,

	@NotBlank(message = "URL은 빈 값일 수 없습니다.")
	String url,

	@Nullable
	List<String> imageUrlList,

	@NotNull(message = "친구 사용자 아이디는 필수 입력값입니다.")
	Long friendUserId,

	@NotBlank(message = "프로필은 빈 값일 수 없습니다.")
	String profile
) {
}
