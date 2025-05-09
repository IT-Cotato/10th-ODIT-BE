package com.adit.backend.domain.place.dto.request;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.adit.backend.domain.place.entity.Place;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Place}
 */

@JsonInclude(NON_NULL)
public record PlaceRequestDto(@NotBlank(message = "장소명은 필수 입력값입니다.")
							  String placeName,

							  @NotNull(message = "위도는 필수 입력값입니다.")
							  BigDecimal latitude,

							  @NotNull(message = "경도는 필수 입력값입니다.")
							  BigDecimal longitude,

							  @NotBlank(message = "지번 주소는 필수 입력값입니다.")
							  String addressName,

							  @NotBlank(message = "도로명 주소는 필수 입력값입니다.")
							  String roadAddressName,

							  @NotBlank(message = "카테고리는 필수 입력값입니다.")
							  String subCategory,

							  @NotBlank(message = "링크는 필수 입력값입니다.")
							  String url,
							  String memo,
							  List<String> imageUrlList)
	implements Serializable {
}