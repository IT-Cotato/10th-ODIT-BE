package com.odit.backend.domain.event.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 응답")
public record EventResponseDto(
	@Schema(
		description = "Event ID",
		example = "2",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Long id,

	@Schema(
		description = "문화정보 API에서 가져온 고유 시퀀스 번호",
		example = "2432452",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Long seq,

	@Schema(
		description = "이벤트명",
		example = "Cats",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String title,

	@Schema(
		description = "카테고리",
		example = "뮤지컬",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String category,

	@Schema(
		description = "이벤트 시작일시",
		example = "2025-07-18",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	LocalDate startDate,

	@Schema(
		description = "이벤트 마감일시",
		example = "2025-07-25",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	LocalDate endDate,

	@Schema(
		description = "사용자 메모",
		example = "꼭 봐야 할 뮤지컬, 친구와 함께 관람 예정",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String memo,

	@Schema(
		description = "방문 여부",
		example = "false",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Boolean visited,

	@Schema(
		description = "관련 이미지 URL 목록",
		example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	List<String> imageUrlList) {
}