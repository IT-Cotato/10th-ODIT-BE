package com.odit.backend.domain.event.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.event.converter.EventConverter;
import com.odit.backend.domain.event.converter.EventStatisticsConverter;
import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.event.entity.EventStatistics;
import com.odit.backend.domain.event.repository.EventRepository;
import com.odit.backend.domain.event.service.facade.EventCommandFacade;
import com.odit.backend.domain.event.service.query.EventQueryService;
import com.odit.backend.domain.event.service.query.EventStatisticsQueryService;
import com.odit.backend.domain.image.service.command.EventImageCommandService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventCommandService {
	private final EventRepository eventRepository;
	private final EventImageCommandService eventImageCommandService;
	private final EventQueryService eventQueryService;
	private final EventCommandFacade eventCommandFacade;

	public Event saveOrFindEvent(EventRequestDto request) {
		return eventQueryService.findEventBySeq(request.seq())
			.map(event -> {
				eventCommandFacade.increaseBookMark(event);
				return event;
			})
			.orElseGet(() -> createNewEvent(request));
	}

	private Event createNewEvent(EventRequestDto request) {
		Event event = EventConverter.toEntity(request);
		Event savedEvent = eventRepository.save(event);
		if (!request.imageUrlList().isEmpty()) {
			eventImageCommandService.addImageToEvent(request, savedEvent);
		}
		eventCommandFacade.createEventStatistics(savedEvent);
		return savedEvent;
	}

}
