package com.odit.backend.infra.async.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 비동기 작업 상태 조회 응답 DTO
 * 변환 로직은 TaskResponseConverter에서 담당합니다.
 */
@Builder
@Schema(description = "비동기 작업 상태 조회 응답")
public record TaskStatusResponse(
	@Schema(description = "작업 ID", example = "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6")
	String taskId,

	@Schema(description = "작업 상태", example = "IN_PROGRESS")
	String status,

	@Schema(description = "상태 메시지", example = "작업 진행 중")
	String statusMessage,

	@Schema(description = "진행률 (0-100)", example = "75")
	int progress,

	@Schema(description = "작업 결과 (완료시)", example = "요약된 내용...")
	Object result,

	@Schema(description = "오류 메시지 (실패시)", example = "네트워크 오류가 발생했습니다.")
	String error,

	@Schema(description = "작업 생성 시간", example = "2025-06-29T10:30:00")
	String createdAt,

	@Schema(description = "작업 완료 시간", example = "2025-06-29T10:40:00")
	String completedAt
) {
}
