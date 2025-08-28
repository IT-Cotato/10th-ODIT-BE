package com.odit.backend.domain.event.service.facade;

import static com.odit.backend.global.error.GlobalErrorCode.EVENT_DELETE_FAILED;

import org.springframework.stereotype.Service;

import com.odit.backend.domain.event.converter.EventStatisticsConverter;
import com.odit.backend.domain.event.converter.UserEventConverter;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.event.entity.EventStatistics;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.domain.event.service.command.EventStatisticsCommandService;
import com.odit.backend.domain.event.service.command.UserEventCommandService;
import com.odit.backend.domain.event.service.query.EventStatisticsQueryService;
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

	private final EventStatisticsCommandService eventStatisticsCommandService;
	private final EventStatisticsQueryService eventStatisticsQueryService;
	private final UserEventQueryService userEventQueryService;
	private final UserEventImageCommandService userEventImageCommandService;
	private final UserEventCommandService userEventCommandService;

	public void createEventStatistics(Event event) {
		EventStatistics eventStatistics = EventStatisticsConverter.toEntity();
		eventStatistics.assignEvent(event);
		EventStatistics statistics = eventStatisticsCommandService.save(eventStatistics);
		event.assignStatics(statistics);
	}

	public void increaseBookMark(Event event) {
		EventStatistics statistics = eventStatisticsQueryService.findById(event.getId());
		statistics.incrementBookmarkCount();
	}

	public void deleteUserEvent(Long id) {
		UserEvent userEvent = userEventQueryService.findById(id);
		Event event = userEvent.getEvent();
		EventStatistics statistics = eventStatisticsQueryService.findById(event.getId());
		try {
			deleteUserEventImages(userEvent);
			userEventCommandService.delete(userEvent);
			statistics.decrementsBookmarkCount();
		} catch (Exception e) {
			throw new EventException(EVENT_DELETE_FAILED); // 실패 시 예외 발생 → 트랜잭션 롤백
		}
	}

	private void deleteUserEventImages(UserEvent userEvent) {
		for (UserEventImage image : userEvent.getImages()) {
			userEventImageCommandService.deleteImage(image.getId());
		}
	}

	public EventResponseDto toggleEventVisitStatus(Long id) {
		UserEvent userEvent = userEventQueryService.findById(id);
		Event event = userEvent.getEvent();
		EventStatistics statistics = eventStatisticsQueryService.findById(event.getId());

		if (userEvent.getVisited()) {
			statistics.decrementVisitCount();
		} else {
			statistics.incrementBookmarkCount();
		}

		return UserEventConverter.toResponse(userEvent);
	}
}
