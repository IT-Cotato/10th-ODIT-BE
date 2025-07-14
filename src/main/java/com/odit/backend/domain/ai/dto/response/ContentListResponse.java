package com.odit.backend.domain.ai.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "AI 분석 결과 콘텐츠 목록 응답")
public record ContentListResponse(

	@Schema(
		description = "AI 분석 결과 콘텐츠 목록",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	List<ContentResponse> contentResponseList,

	@Schema(
		description = "관련 이미지 URL 목록",
		example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	List<String> imageSrcList
) {
}