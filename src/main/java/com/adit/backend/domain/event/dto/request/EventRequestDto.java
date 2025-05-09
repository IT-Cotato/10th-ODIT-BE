package com.adit.backend.domain.event.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record EventRequestDto(
	@NotBlank(message = "이벤트명은 필수 입력값입니다.")
	String name,

	@NotBlank(message = "카테고리는 필수 입력값입니다.")
	String category,

	LocalDateTime startDate,
	LocalDateTime endDate,
	String memo,
	Boolean visited,
	List<String> imageUrlList
) {
}