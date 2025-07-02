package com.odit.backend.domain.event.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventUpdateRequestDto(
    @NotBlank(message = "이벤트명은 필수 입력값입니다.")
    String name,

    @NotBlank(message = "카테고리는 필수 입력값입니다.")
    String category,

    @NotNull(message = "시작일은 필수 입력값입니다.")
    LocalDateTime startDate,

    @NotNull(message = "종료일은 필수 입력값입니다.")
    LocalDateTime endDate,

    String memo,

    Boolean visited
) {
}