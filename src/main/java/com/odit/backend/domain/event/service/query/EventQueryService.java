package com.odit.backend.domain.event.service.query;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.domain.event.repository.EventRepository;
import com.odit.backend.global.error.GlobalErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventQueryService {

	private final EventRepository eventRepository;

	public Event getEventByTitle(String eventName) {
		return eventRepository.findByTitle(eventName)
			.orElseThrow(() -> new EventException(GlobalErrorCode.COMMON_EVENT_NOT_FOUND));
	}

	public Optional<Event> getEventByExternalId(long externalId) {
		return eventRepository.findByExternalId(externalId);
	}
}
