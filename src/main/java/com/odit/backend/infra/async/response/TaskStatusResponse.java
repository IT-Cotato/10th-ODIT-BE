package com.odit.backend.infra.async.response;

import com.odit.backend.domain.ai.dto.response.ContentListResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 비동기 작업 상태 조회 응답 DTO
 * 변환 로직은 TaskResponseConverter에서 담당합니다.
 */
@Builder
@Schema(description = "비동기 문화 정보 AI 분석 작업 상태 조회 응답")
public record TaskStatusResponse(

	@Schema(
		description = "작업 고유 식별자",
		example = "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String taskId,

	@Schema(
		description = "작업 상태 (PENDING: 대기중, IN_PROGRESS: 진행중, COMPLETED: 완료, FAILED: 실패)",
		example = "IN_PROGRESS",
		requiredMode = Schema.RequiredMode.REQUIRED,
		allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED", "FAILED"}
	)
	String status,

	@Schema(
		description = "현재 작업 단계에 대한 상태 메시지",
		example = "문화 콘텐츠 분석 중...",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String statusMessage,

	@Schema(
		description = "작업 진행률 (0-100)",
		example = "75",
		minimum = "0",
		maximum = "100",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	int progress,

	@Schema(
		description = "AI 분석 결과 (작업 완료시에만 반환)",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	ContentListResponse result,

	@Schema(
		description = "오류 메시지 (작업 실패시에만 반환)",
		example = "네트워크 연결 오류가 발생했습니다.",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String error,

	@Schema(
		description = "작업 생성 시간 ",
		example = "2025-06-29T10:30:00",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String createdAt,

	@Schema(
		description = "작업 완료 시간, 완료시에만 반환)",
		example = "2025-06-29T10:40:00",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String completedAt
) {
}