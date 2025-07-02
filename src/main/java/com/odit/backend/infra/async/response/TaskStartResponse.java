package com.odit.backend.infra.async.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 비동기 작업 시작 응답 DTO
 */
@Builder
@Schema(description = "비동기 작업 시작 응답")
public record TaskStartResponse(
	@Schema(description = "작업 ID", example = "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6")
	String taskId,

	@Schema(description = "작업 상태", example = "PENDING")
	String status,

	@Schema(description = "작업 생성 시간", example = "2025-06-29T10:30:00")
	String createdAt,

	@Schema(description = "상태 메시지", example = "작업이 성공적으로 시작되었습니다.")
	String message
) {
}
