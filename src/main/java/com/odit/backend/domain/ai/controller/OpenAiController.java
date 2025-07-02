package com.odit.backend.domain.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odit.backend.domain.ai.dto.request.ContentExtractionRequest;
import com.odit.backend.domain.ai.dto.response.CrawlCompletionResponse;
import com.odit.backend.domain.ai.service.ContentService;
import com.odit.backend.domain.ai.service.OpenAiService;
import com.odit.backend.global.common.ApiResponse;
import com.odit.backend.infra.async.converter.TaskResponseConverter;
import com.odit.backend.infra.async.entity.SummaryTask;
import com.odit.backend.infra.async.response.TaskStartResponse;
import com.odit.backend.infra.async.response.TaskStatusResponse;
import com.odit.backend.infra.async.service.SummaryTaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Tag(name = "AI Summary API", description = "웹 페이지 크롤링 및 OpenAI를 활용한 문화/외식 정보 추출 API")
public class OpenAiController {

	private final OpenAiService openAiService;
	private final ContentService contentService;
	private final SummaryTaskService summaryTaskService;
	private final TaskResponseConverter taskResponseConverter;


	@Operation(
		summary = "비동기 AI 요약 작업 시작",
		description = "URL 기반 문화/외식 정보 분석을 비동기로 시작하고 작업 ID를 반환합니다. 이후 /{taskId}로 진행 상황을 확인할 수 있습니다."
	)
	@PostMapping("/summary")
	public ResponseEntity<ApiResponse<TaskStartResponse>> startSummaryAsync(
		@Valid @RequestBody final ContentExtractionRequest request) {
		SummaryTask task = summaryTaskService.createTask(request.url());
		openAiService.startSummaryAsync(task.getId(), request.url());

		TaskStartResponse response = taskResponseConverter.toTaskStartResponse(task);

		log.info("비동기 요약 작업 시작됨 - TaskId: {}, URL: {}, Status: {}",
			task.getId(), request.url(), task.getStatus().getMessage());

		return ResponseEntity.accepted().body(ApiResponse.success(response));
	}

	@Operation(
		summary = "작업 상태 조회",
		description = "작업 ID를 통해 현재 진행 상황, 완료 여부, 결과를 조회합니다."
	)
	@GetMapping("/summary/{taskId}")
	public ResponseEntity<ApiResponse<TaskStatusResponse>> getTaskStatus(
		@PathVariable("taskId") String taskId) {
		SummaryTask task = summaryTaskService.findTask(taskId);
		TaskStatusResponse response = taskResponseConverter.toTaskStatusResponse(task);

		log.debug("작업 상태 조회 - TaskId: {}, Status: {}", taskId, task.getStatus().getMessage());

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@Operation(
		summary = "작업 취소",
		description = "진행 중인 작업을 취소합니다."
	)
	@DeleteMapping("/summary/{taskId}")
	public ResponseEntity<Void> cancelTask(
		@PathVariable("taskId") String taskId) {
		summaryTaskService.cancelTask(taskId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "URL 크롤링 테스트",
		description = "입력된 URL의 웹 페이지 크롤링을 수행하여 추출된 원본 컨텐츠를 반환합니다."
	)
	@PostMapping("/crawl")
	public ResponseEntity<ApiResponse<CrawlCompletionResponse>> crawlPage(
		@Valid @RequestBody final ContentExtractionRequest request) {
		return ResponseEntity.ok(ApiResponse.success(contentService.extractContents(request.url()).join()));
	}
}
