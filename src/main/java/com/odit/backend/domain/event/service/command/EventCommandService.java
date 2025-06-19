package com.odit.backend.domain.event.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.event.converter.EventConverter;
import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.event.repository.EventRepository;
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
	private final EventConverter eventConverter;
	private final EventImageCommandService eventImageCommandService;

	public Event saveOrFindEvent(EventRequestDto request) {
		return eventRepository.findByName(request.name()).orElseGet(() -> {
			Event event = eventConverter.toEntity(request);
			if (!request.imageUrlList().isEmpty()) {
				eventImageCommandService.addImageToEvent(request, event);
			}
			return eventRepository.save(event);
		});
	}
}
