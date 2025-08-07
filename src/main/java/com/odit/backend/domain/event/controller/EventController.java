package com.odit.backend.domain.event.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.dto.response.UserEventImageResponseDto;
import com.odit.backend.domain.event.service.command.UserEventCommandService;
import com.odit.backend.domain.event.service.query.UserEventQueryService;
import com.odit.backend.domain.image.dto.request.ImageUpdateRequestDto;
import com.odit.backend.domain.image.service.command.UserEventImageCommandService;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "이벤트 데이터를 생성, 수정, 조회할 수 있는 API입니다.")
public class EventController {

	private final UserEventCommandService commandService;
	private final UserEventQueryService queryService;
	private final UserEventImageCommandService userEventImageCommandService;

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

	@Operation(summary = "새 이벤트 생성", description = "제공된 세부 정보를 기반으로 새 이벤트를 생성합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "이벤트 생성 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@PostMapping
	public ResponseEntity<ApiResponse<EventResponseDto>> createEvent(
		@AuthenticationPrincipal(expression = "user") User user,
		@RequestBody EventRequestDto request) {
		EventResponseDto event = commandService.createUserEvent(request, user.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(event));
	}

	@Operation(summary = "이벤트 기본 정보 수정", description = "기존 이벤트의 기본 정보를 수정합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 정보 수정 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<EventResponseDto>> updateEventInfo(
		@PathVariable Long id, @Valid @RequestBody EventUpdateRequestDto request) {

		EventResponseDto updatedEvent = commandService.updateEventInfo(id, request);
		return ResponseEntity.ok(ApiResponse.success(updatedEvent));
	}

	@Operation(summary = "이벤트 메모 수정", description = "기존 이벤트의 메모를 수정합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 메모 수정 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@PatchMapping("/{id}/memo")
	public ResponseEntity<ApiResponse<EventResponseDto>> updateEventMemo(
		@PathVariable Long id, @RequestParam String memo) {

		EventResponseDto updatedEvent = commandService.updateUserEventMemo(id, memo);
		return ResponseEntity.ok(ApiResponse.success(updatedEvent));
	}

	@Operation(summary = "이벤트 삭제", description = "이벤트 ID를 기반으로 해당 이벤트를 삭제합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "204",
			description = "이벤트 삭제 성공"
		)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
		commandService.deleteEvent(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "유저 이벤트 이미지 추가", description = "유저 이벤트에 새로운 이미지를 추가합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 이미지 추가 성공",
			content = @Content(schema = @Schema(implementation = UserEventImageResponseDto.class))
		)
	})
	@PostMapping("/{id}/images")
	public ResponseEntity<ApiResponse<List<UserEventImageResponseDto>>> addEventImages(
		@PathVariable Long id,
		@RequestPart List<MultipartFile> images) {

		List<UserEventImageResponseDto> uploadedImages = userEventImageCommandService.addUserEventImages(id, images);
		return ResponseEntity.ok(ApiResponse.success(uploadedImages));
	}

	@Operation(summary = "유저 이벤트 이미지 업데이트", description = "유저 이벤트의 기존 이미지를 새로운 이미지로 교체합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 이미지 업데이트 성공",
			content = @Content(schema = @Schema(implementation = UserEventImageResponseDto.class))
		)
	})
	@PutMapping("/{id}/images")
	public ResponseEntity<ApiResponse<List<UserEventImageResponseDto>>> updateEventImages(
		@PathVariable Long id,
		@Valid @RequestBody List<ImageUpdateRequestDto> imageUpdateRequests) {

		List<UserEventImageResponseDto> updatedImages = userEventImageCommandService.updateUserEventImages(id,
			imageUpdateRequests);
		return ResponseEntity.ok(ApiResponse.success(updatedImages));
	}

	@Operation(summary = "유저 이벤트 이미지 삭제", description = "유저 이벤트의 특정 이미지를 삭제합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "204",
			description = "이벤트 이미지 삭제 성공"
		)
	})
	@DeleteMapping("/{id}/images/{imageId}")
	public ResponseEntity<ApiResponse<Void>> deleteEventImage(
		@PathVariable Long id, @PathVariable Long imageId) {

		userEventImageCommandService.deleteEventImage(id, imageId);
		return ResponseEntity.noContent().build();
	}
}