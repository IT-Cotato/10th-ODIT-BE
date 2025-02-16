package com.adit.backend.domain.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.notification.service.NotificationDemoDayService;
import com.adit.backend.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Tag(name = "데모데이 API", description = "데모데이를 위한 API 입니다 다들 고생하셨습니다 :D")

public class DemoDayController {

	private final NotificationDemoDayService notificationDemoDayService;

	@PostMapping("/duration")
	@Operation(summary = "데모데이 누락 기간 알림", description = "원영이형한테 보내봐요!")
	public ResponseEntity<ApiResponse<String>> sendDuration() {
		notificationDemoDayService.sendDurationNotification();
		return ResponseEntity.ok(ApiResponse.success("아마도 잘 보내졌을거에요!"));
	}

	@PostMapping("/unvisited")
	@Operation(summary = "데모데이 미방문 장소 알림", description = "원영이형한테 보내봐요!")
	public ResponseEntity<ApiResponse<String>> sendUnvisited() {
		notificationDemoDayService.sendUnvisitedNotification();
		return ResponseEntity.ok(ApiResponse.success("아마도 잘 보내졌을거에요!"));
	}

	@PostMapping("/start")
	@Operation(summary = "데모데이 이벤트 시작 알림", description = "원영이형한테 보내봐요!")
	public ResponseEntity<ApiResponse<String>> sendStartNotification() {
		notificationDemoDayService.sendStartNotification();
		return ResponseEntity.ok(ApiResponse.success("아마도 잘 보내졌을거에요!"));
	}


}
