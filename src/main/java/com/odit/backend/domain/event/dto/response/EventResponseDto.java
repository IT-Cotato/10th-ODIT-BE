package com.odit.backend.domain.event.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 응답")
public record EventResponseDto(
	@Schema(
		description = "Event ID",
		example = "2"
	)
	Long id,
	@Schema(
		description = "이벤트명",
		example = "Cats"
	)
	String title,

	@Schema(
		description = "카테고리",
		example = "뮤지컬"
	)
	String category,

	@Schema(
		description = "이벤트 시작일시",
		example = "20250718"
	)
	LocalDate startDate,

	@Schema(
		description = "이벤트 마감일시",
		example = "20250718"
	)
	LocalDate endDate,

	@Schema(
		description = "사용자 메모"
	)
	String memo,

	@Schema(
		description = "방문 여부",
		example = "false"
	)
	Boolean visited,

	@Schema(
		description = "관련 이미지 URL 목록",
		example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]"
	)
	List<String> imageUrlList) {
}