package com.odit.backend.domain.event.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "이벤트 등록 요청")
public record EventRequestDto(
	@Schema(
		description = "문화정보 API에서 가져온 ",
		example = "2432452"
	)
	Long seq,
	@NotBlank(message = "이벤트명은 필수 입력값입니다.")
	@Schema(
		description = "이벤트명",
		example = "Cats",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String title,

	@NotBlank(message = "카테고리는 필수 입력값입니다.")
	@Schema(
		description = "카테고리",
		example = "뮤지컬",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String category,

	@NotBlank(message = "이벤트 시작일자는 필수 입력값입니다.")
	@Schema(
		description = "이벤트 시작일시",
		example = "20250718",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@JsonFormat(pattern = "yyyyMMdd")
	LocalDate startDate,

	@NotNull(message = "이벤트 종료일자는 필수 입력값입니다.")
	@Schema(
		description = "이벤트 마감일시",
		example = "20250718",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@JsonFormat(pattern = "yyyyMMdd")
	LocalDate endDate,

	@Schema(
		description = "사용자 메모",
		example = "",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String memo,

	@Schema(
		description = "관련 이미지 URL 목록",
		example = "[\"https://avatars.githubusercontent.com/in/946600?s=64&v=4\"]",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	List<String> imageUrlList
) {
}