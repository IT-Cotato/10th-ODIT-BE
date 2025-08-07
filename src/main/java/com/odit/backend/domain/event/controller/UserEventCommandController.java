package com.odit.backend.domain.event.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.service.command.UserEventCommandService;
import com.odit.backend.domain.event.service.facade.EventCommandFacade;
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
public class UserEventCommandController {

	private final UserEventCommandService commandService;
	private final EventCommandFacade eventCommandFacade;

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
		eventCommandFacade.deleteUserEvent(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "이벤트 방문 상태 전환", description = "이벤트의 방문 완료 상태를 전환합니다. (방문 <-> 미방문)")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 방문 상태 전환 성공",
			content = @Content(schema = @Schema(implementation = EventResponseDto.class))
		)
	})
	@PatchMapping("/{id}/visit-status")
	public ResponseEntity<ApiResponse<EventResponseDto>> toggleEventVisitStatus(
		@PathVariable Long id) {
		EventResponseDto updatedEvent = eventCommandFacade.toggleEventVisitStatus(id);
		return ResponseEntity.ok(ApiResponse.success(updatedEvent));
	}
}