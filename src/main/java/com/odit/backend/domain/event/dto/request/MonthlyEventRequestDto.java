package com.odit.backend.domain.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "월별 이벤트 조회 요청")
public record MonthlyEventRequestDto(
	@Schema(
		description = "조회할 연도",
		example = "2025",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull
	@Min(value = 2000, message = "연도는 2000년 이상이어야 합니다.")
	@Max(value = 3000, message = "연도는 3000년 이하여야 합니다.")
	Integer year,

	@Schema(
		description = "조회할 월 (1-12)",
		example = "8",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull
	@Min(value = 1, message = "월은 1 이상이어야 합니다.")
	@Max(value = 12, message = "월은 12 이하여야 합니다.")
	Integer month
) {
}