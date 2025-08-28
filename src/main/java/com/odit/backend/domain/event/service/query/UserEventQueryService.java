package com.odit.backend.domain.event.service.query;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.odit.backend.domain.event.converter.UserEventConverter;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.domain.event.repository.UserEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventQueryService {

	private final UserEventRepository userEventRepository;
	private final UserEventConverter userEventConverter;

	public List<EventResponseDto> getAllEvents() {
		return userEventRepository.findAll()
			.stream()
			.map(userEventConverter::toResponse)
			.toList();
	}

	public EventResponseDto getEventById(Long id) {
		UserEvent userEvent = userEventRepository.findById(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));
		return userEventConverter.toResponse(userEvent);
	}

	public List<EventResponseDto> getEventsByDate(LocalDate date) {
		return userEventRepository.findByDate(date)
			.stream()
			.map(userEventConverter::toResponse)
			.toList();
	}

	public List<EventResponseDto> getTodayEvents() {
		LocalDate today = LocalDate.now();
		return getEventsByDate(today);
	}

	public List<EventResponseDto> getNoDateEvents() {
		return userEventRepository.findNoDateEvents()
			.stream()
			.map(userEventConverter::toResponse)
			.toList();
	}

	public List<EventResponseDto> getPopularEvents() {
		return userEventRepository.findPopularEvents()
			.stream()
			.map(userEventConverter::toResponse)
			.toList();
	}
}