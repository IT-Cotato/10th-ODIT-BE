package com.odit.backend.domain.event.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.odit.backend.domain.event.converter.EventConverter;
import com.odit.backend.domain.event.dto.request.MonthlyEventRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.dto.response.MonthlyEventPageResponseDto;
import com.odit.backend.domain.event.service.facade.EventQueryFacade;
import com.odit.backend.domain.event.service.query.UserEventQueryService;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "이벤트 데이터를 생성, 수정, 조회할 수 있는 API입니다.")
public class UserEventQueryController {

	private final UserEventQueryService queryService;
	private final EventQueryFacade eventQueryFacade;

	@Operation(summary = "모든 이벤트 조회", description = "유저의 모든 이벤트 목록을 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<EventResponseDto>>> getAllEvents(
		@AuthenticationPrincipal(expression = "user") User user) {
		List<EventResponseDto> events = queryService.getAllUserEvents(user.getId());
		return ResponseEntity.ok(ApiResponse.success(events));
	}

	//특정 ID로 이벤트 조회
	@Operation(summary = "ID로 이벤트 조회", description = "특정 ID에 해당하는 이벤트의 세부 정보를 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 조회 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<EventResponseDto>> getEventById(@PathVariable Long id) {
		EventResponseDto event = queryService.getEventById(id);
		return ResponseEntity.ok(ApiResponse.success(event));
	}

	@Operation(summary = "특정 날짜의 이벤트 조회", description = "특정 날짜에 해당하는 이벤트 목록을 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "날짜별 이벤트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@GetMapping("/date")
	public ResponseEntity<ApiResponse<List<EventResponseDto>>> getEventsByDate(@RequestParam LocalDate date) {
		List<EventResponseDto> events = queryService.getEventsByDate(date);
		return ResponseEntity.ok(ApiResponse.success(events));
	}

	@Operation(summary = "오늘의 이벤트 조회", description = "오늘 날짜에 해당하는 이벤트 목록을 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "오늘의 이벤트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@GetMapping("/today")
	public ResponseEntity<ApiResponse<List<EventResponseDto>>> getTodayEvents() {
		List<EventResponseDto> events = queryService.getTodayEvents();
		return ResponseEntity.ok(ApiResponse.success(events));
	}

	@Operation(summary = "인기 이벤트 조회", description = "방문 수를 기준으로 인기 있는 이벤트 목록을 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "인기 이벤트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@GetMapping("/popular")
	public ResponseEntity<ApiResponse<List<EventResponseDto>>> getPopularEvents() {
		List<EventResponseDto> events = queryService.getPopularEvents();
		return ResponseEntity.ok(ApiResponse.success(events));
	}

	@Operation(summary = "월별 이벤트 페이지 조회", description = "특정 연월의 사용자 이벤트 목록을 페이지 형태로 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "월별 이벤트 페이지 조회 성공",
			content = @Content(schema = @Schema(implementation = MonthlyEventPageResponseDto.class))
		)
	})
	@GetMapping("/monthly")
	public ResponseEntity<ApiResponse<MonthlyEventPageResponseDto>> getMonthlyEvents(
		@AuthenticationPrincipal(expression = "user") User user,
		@RequestParam
		@Min(value = 2000, message = "연도는 2000년 이상이어야 합니다.")
		@Max(value = 3000, message = "연도는 3000년 이하여야 합니다.")
		Integer year,
		@RequestParam
		@Min(value = 1, message = "월은 1 이상이어야 합니다.")
		@Max(value = 12, message = "월은 12 이하여야 합니다.")
		Integer month,
		Pageable pageable) {
		{
			MonthlyEventRequestDto request = EventConverter.toMonthlyRequest(year, month);
			MonthlyEventPageResponseDto response = eventQueryFacade.getUserEventsByMonth(user.getId(), request,
				pageable);
			return ResponseEntity.ok(ApiResponse.success(response));
		}
	}
}