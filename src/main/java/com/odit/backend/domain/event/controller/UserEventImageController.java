package com.odit.backend.domain.event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.odit.backend.domain.event.dto.response.UserEventImageResponseDto;
import com.odit.backend.domain.image.dto.request.ImageUpdateRequestDto;
import com.odit.backend.domain.image.service.command.UserEventImageCommandService;
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
@Tag(name = "Event Image API", description = "유저 이벤트 이미지를 생성, 수정, 삭제할 수 있는 API입니다.")
public class UserEventImageController {
	private final UserEventImageCommandService userEventImageCommandService;

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

	@Operation(summary = "유저 이벤트 단일 이미지 삭제", description = "유저 이벤트의 특정 이미지를 삭제합니다.")
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
