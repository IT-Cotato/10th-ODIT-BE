package com.odit.backend.domain.event.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "이벤트 업데이트 요청")
public record EventUpdateRequestDto(
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

	@NotNull(message = "이벤트 시작일자는 필수 입력값입니다.")
	@Schema(
		description = "이벤트 시작일시",
		example = "20250718",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	LocalDate startDate,

	@NotNull(message = "이벤트 종료일자는 필수 입력값입니다.")
	@Schema(
		description = "이벤트 마감일시",
		example = "20250718",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	LocalDate endDate,

	@Schema(
		description = "사용자 메모",
		example = "",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String memo,

	@Schema(
		description = "방문 여부",
		example = "false",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	Boolean visited
) {
}