package com.odit.backend.domain.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "이벤트 메모 업데이트 요청")
public record EventMemoUpdateRequestDto(
	@NotBlank(message = "메모는 공백일 수 없습니다.")
	@Size(max = 500, message = "메모는 500자를 초과할 수 없습니다.")
	@Schema(
		description = "이벤트 메모",
		example = "정말 좋은 이벤트였습니다!",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String memo
) {
}