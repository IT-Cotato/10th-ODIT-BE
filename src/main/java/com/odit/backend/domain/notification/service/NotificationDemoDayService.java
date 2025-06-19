package com.odit.backend.domain.notification.service;

import org.springframework.stereotype.Service;

import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.domain.event.repository.UserEventRepository;
import com.odit.backend.domain.notification.converter.NotificationEventConverter;
import com.odit.backend.domain.notification.event.NotificationEvent;
import com.odit.backend.domain.notification.service.command.NotificationCommandService;
import com.odit.backend.domain.place.entity.UserPlace;
import com.odit.backend.domain.place.exception.PlaceException;
import com.odit.backend.domain.place.repository.UserPlaceRepository;
import com.odit.backend.domain.place.service.query.UserPlaceQueryService;
import com.odit.backend.global.error.GlobalErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데모데이를 위한 서비스입니다 😅
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationDemoDayService {
	private final NotificationEventConverter eventConverter;
	private final UserPlaceQueryService userPlaceQueryService;
	private final UserPlaceRepository userPlaceRepository;
	private final NotificationCommandService notificationCommandService;
	private final UserEventRepository userEventRepository;

	/**
	 *  원영이형한테 기간입력하라고 알림보내기
	 *  1. 원영이형의 유저 ID == 1
	 *  2. 기간 미입력된 이벤트를 ID 1 로 정의
	 */
	public void sendDurationNotification() {
		UserEvent userEvent = userEventRepository.findById(1L)
			.orElseThrow(() -> new EventException(GlobalErrorCode.EVENT_NOT_FOUND));
		NotificationEvent event = eventConverter.toMissingEvent(userEvent);
		notificationCommandService.sendNotification(event);
	}

	/**
	 *  원영이형한테 방문하라고 알림보내기
	 *  1. 원영이형의 유저 ID == 1
	 *  2. 알림을 보내고싶은 임의의 장소를 ID 1 로 정의
	 */
	public void sendUnvisitedNotification() {
		UserPlace userPlace = userPlaceRepository.findById(1L)
			.orElseThrow(() -> new PlaceException(GlobalErrorCode.USER_PLACE_NOT_FOUND));
		NotificationEvent event = eventConverter.toUnvisitedEvent(userPlace);
		notificationCommandService.sendNotification(event);
	}


	/**
	 *  원영이형한테 방문하라고 알림보내기
	 *  1. 원영이형의 유저 ID == 1
	 *  2. 알림을 보내고싶은 임의의 장소를 시작 일이 데모데이 기준 3일 남도록 데이터 저장
	 */
	public void sendStartNotification() {
		UserEvent userEvent = userEventRepository.findById(1L)
			.orElseThrow(() -> new EventException(GlobalErrorCode.EVENT_NOT_FOUND));
		NotificationEvent event = eventConverter.toStartEvent(userEvent, 3);
		notificationCommandService.sendNotification(event);
	}

}
