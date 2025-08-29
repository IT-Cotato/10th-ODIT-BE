package com.odit.backend.domain.event.service.query;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.odit.backend.domain.event.converter.UserEventConverter;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.dto.response.MonthlyEventPageResponseDto;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.domain.event.repository.UserEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventQueryService {

	private final UserEventRepository userEventRepository;

	public UserEvent findById(Long id) {
		return userEventRepository.findById(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));
	}

	public List<EventResponseDto> getAllEvents() {
		List<EventResponseDto> events = userEventRepository.findAll()
			.stream()
			.map(UserEventConverter::toResponse)
			.toList();
		log.info("[Event] 전체 이벤트 조회 완료 | eventCount = {}", events.size());
		return events;
	}

	public List<EventResponseDto> getAllUserEvents(Long userId) {
		List<EventResponseDto> userEvents = userEventRepository.findAllUserEvents(userId)
			.stream()
			.map(UserEventConverter::toResponse)
			.toList();
		log.info("[Event] 사용자 이벤트 조회 완료 | userId = {}, eventCount = {}", userId, userEvents.size());
		return userEvents;
	}

	public EventResponseDto getEventById(Long id) {
		UserEvent userEvent = userEventRepository.findById(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));
		EventResponseDto response = UserEventConverter.toResponse(userEvent);
		log.info("[Event] 이벤트 상세 조회 완료 | eventId = {}", id);
		return response;
	}

	public List<EventResponseDto> getEventsByDate(LocalDate date) {
		List<EventResponseDto> events = userEventRepository.findByDate(date)
			.stream()
			.map(UserEventConverter::toResponse)
			.toList();
		log.info("[Event] 날짜별 이벤트 조회 완료 | date = {}, eventCount = {}", date, events.size());
		return events;
	}

	public List<EventResponseDto> getTodayEvents() {
		LocalDate today = LocalDate.now();
		List<EventResponseDto> todayEvents = getEventsByDate(today);
		log.info("[Event] 오늘 이벤트 조회 완료 | date = {}, eventCount = {}", today, todayEvents.size());
		return todayEvents;
	}

	public List<EventResponseDto> getPopularEvents() {
		List<EventResponseDto> popularEvents = userEventRepository.findPopularEvents()
			.stream()
			.map(UserEventConverter::toResponse)
			.toList();
		log.info("[Event] 인기 이벤트 조회 완료 | eventCount = {}", popularEvents.size());
		return popularEvents;
	}

	public MonthlyEventPageResponseDto getUserEventsByMonth(Long userId, Integer year, Integer month, Pageable pageable) {
		Page<UserEvent> userEventPage = userEventRepository.findUserEventsByMonth(userId, year, month, pageable);
		Page<EventResponseDto> eventResponsePage = userEventPage.map(UserEventConverter::toResponse);
		log.info("[Event] 월별 이벤트 조회 완료 | userId = {}, year = {}, month = {}, pageNumber = {}, totalElements = {}", 
			userId, year, month, pageable.getPageNumber(), eventResponsePage.getTotalElements());
		return MonthlyEventPageResponseDto.from(eventResponsePage, year, month);
	}
}