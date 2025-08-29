package com.odit.backend.domain.event.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "월별 이벤트 페이지 응답")
public record MonthlyEventPageResponseDto(
	@Schema(
		description = "이벤트 목록",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	List<EventResponseDto> content,

	@Schema(
		description = "현재 페이지 번호 (0부터 시작)",
		example = "0",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer pageNumber,

	@Schema(
		description = "페이지당 요소 개수",
		example = "10",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer pageSize,

	@Schema(
		description = "전체 요소 개수",
		example = "50",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Long totalElements,

	@Schema(
		description = "전체 페이지 개수",
		example = "5",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer totalPages,

	@Schema(
		description = "첫 번째 페이지 여부",
		example = "true",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Boolean first,

	@Schema(
		description = "마지막 페이지 여부",
		example = "false",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Boolean last,

	@Schema(
		description = "비어있는 페이지 여부",
		example = "false",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Boolean empty,

	@Schema(
		description = "조회한 연도",
		example = "2025",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer year,

	@Schema(
		description = "조회한 월",
		example = "8",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer month
) {

	public static MonthlyEventPageResponseDto from(Page<EventResponseDto> page, Integer year, Integer month) {
		return MonthlyEventPageResponseDto.builder()
			.content(page.getContent())
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.first(page.isFirst())
			.last(page.isLast())
			.empty(page.isEmpty())
			.year(year)
			.month(month)
			.build();
	}
}