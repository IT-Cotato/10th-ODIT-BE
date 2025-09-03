package com.odit.backend.domain.event.service.facade;

import static com.odit.backend.global.error.GlobalErrorCode.EVENT_DELETE_FAILED;

import com.odit.backend.global.error.GlobalErrorCode;

import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Service;

import com.odit.backend.domain.event.converter.UserEventConverter;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.event.entity.EventStatistics;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.domain.event.service.command.UserEventCommandService;
import com.odit.backend.domain.event.service.query.UserEventQueryService;
import com.odit.backend.domain.image.entity.UserEventImage;
import com.odit.backend.domain.image.service.command.UserEventImageCommandService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventCommandFacade {

	private final UserEventQueryService userEventQueryService;
	private final UserEventImageCommandService userEventImageCommandService;
	private final UserEventCommandService userEventCommandService;


	public void deleteUserEvent(Long id, Long userId) {
		UserEvent userEvent = userEventQueryService.findByIdAndUserId(id, userId);
		Event event = userEvent.getEvent();
		EventStatistics statistics = event.getEventStatistics();
		if (statistics == null) {
			throw new EventException(GlobalErrorCode.MISSING_EVENT_STATISTICS);
		}
		try {
			deleteUserEventImages(userEvent);
			userEventCommandService.delete(userEvent);
			statistics.decrementBookmarkCount();
		} catch (DataAccessException | EventException e) {
			log.error("삭제 작업 실패: {}", e.getMessage());
			throw new EventException(EVENT_DELETE_FAILED);
		} catch (Exception e) {
			log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
			throw new EventException(EVENT_DELETE_FAILED);
		}
	}

	private void deleteUserEventImages(UserEvent userEvent) {
		for (UserEventImage image : userEvent.getImages()) {
			userEventImageCommandService.deleteImage(image.getId());
		}
	}

	public EventResponseDto toggleEventVisitStatus(Long id, Long userId) {
		UserEvent userEvent = userEventQueryService.findByIdAndUserId(id, userId);
		Event event = userEvent.getEvent();
		EventStatistics statistics = event.getEventStatistics();
		if (statistics == null) {
			throw new EventException(GlobalErrorCode.MISSING_EVENT_STATISTICS);
		}

		if (Boolean.TRUE.equals(userEvent.getVisited())) {
			statistics.decrementVisitCount();
			userEvent.toggleVisited();
		} else {
			statistics.incrementVisitCount();
			userEvent.toggleVisited();
		}

		return UserEventConverter.toResponse(userEvent);
	}
}