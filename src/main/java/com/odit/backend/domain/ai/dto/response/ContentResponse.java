package com.odit.backend.domain.ai.dto.response;

import com.odit.backend.domain.ai.enums.ContentType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "문화 정보 AI 분석 결과 응답")
public record ContentResponse(

	@Schema(
		description = "장소명 또는 이벤트명",
		example = "국립현대미술관 서울관",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String name,

	@Schema(
		description = "문화 콘텐츠 유형 (RESTAURANT: 음식점/카페, EXHIBITION: 전시회/박물관/갤러리/팝업, PERFORMANCE: 공연/연극/콘서트)",
		example = "EXHIBITION",
		requiredMode = Schema.RequiredMode.REQUIRED,
		allowableValues = {"RESTAURANT", "EXHIBITION", "PERFORMANCE"}
	)
	ContentType type,

	@Schema(
		description = "장소 전체 주소 (정보가 없는 경우 null)",
		example = "서울특별시 종로구 삼청로 30",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String location,

	@Schema(
		description = "기간 정보 (전시 기간, 공연 일정, 운영 시간 등, 정보가 없는 경우 null)",
		example = "2024.01.15 - 2024.03.31",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String period
) {
}