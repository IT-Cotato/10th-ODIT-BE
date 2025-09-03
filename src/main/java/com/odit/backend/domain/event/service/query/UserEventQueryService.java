package com.odit.backend.domain.event.service.query;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.odit.backend.domain.event.converter.EventConverter;
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
	private static final int MIN_VALID_YEAR = 1900;
	private static final int MAX_VALID_YEAR = 2100;
	private static final int MIN_VALID_MONTH = 1;
	private static final int MAX_VALID_MONTH = 12;

	public UserEvent findById(Long id) {
		return userEventRepository.findByIdWithEvent(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));
	}

	public UserEvent findByIdAndUserId(Long id, Long userId) {
		return userEventRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));
	}

	public List<EventResponseDto> getAllEvents() {
		List<EventResponseDto> events = userEventRepository.findAllWithEvent()
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
		UserEvent userEvent = userEventRepository.findByIdWithEvent(id)
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
		validateYearMonthParameters(year, month);

		YearMonth targetYearMonth = createYearMonth(year, month);
		LocalDateTime[] monthDateRange = calculateMonthDateRange(targetYearMonth);

		Pageable safePageable = createSafePageable(pageable);

		Page<UserEvent> userEventPage = fetchUserEventsByMonth(userId, monthDateRange[0], monthDateRange[1], safePageable);
		Page<EventResponseDto> eventResponsePage = convertToEventResponsePage(userEventPage);

		log.info("[Event] 월별 이벤트 조회 완료 | userId = {}, year = {}, month = {}, pageNumber = {}, totalElements = {}",
			userId, year, month, pageable.getPageNumber(), eventResponsePage.getTotalElements());
		return EventConverter.toMonthlyEventPageResponseDto(eventResponsePage, year, month);
	}

	private void validateYearMonthParameters(Integer year, Integer month) {
		validateNotNull(year, month);
		validateMonthRange(month);
		validateYearRange(year);
	}

	private void validateNotNull(Integer year, Integer month) {
		if (year == null || month == null) {
			throw new EventException(INVALID_YEAR_MONTH_PARAMETER);
		}
	}

	private void validateMonthRange(Integer month) {
		if (month < MIN_VALID_MONTH || month > MAX_VALID_MONTH) {
			throw new EventException(INVALID_MONTH_RANGE);
		}
	}

	private void validateYearRange(Integer year) {
		if (year < MIN_VALID_YEAR || year > MAX_VALID_YEAR) {
			throw new EventException(INVALID_YEAR_RANGE);
		}
	}

	private LocalDateTime[] calculateMonthDateRange(YearMonth yearMonth) {
		LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endDateTime = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
		return new LocalDateTime[] {startDateTime, endDateTime};
	}


	private YearMonth createYearMonth(Integer year, Integer month) {
		try {
			return YearMonth.of(year, month);
		} catch (Exception e) {
			log.error("[Event] YearMonth 생성 실패 | year = {}, month = {}", year, month, e);
			throw new EventException(INVALID_YEAR_MONTH_PARAMETER);
		}
	}

	private Pageable createSafePageable(Pageable pageable) {
		return PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize()
		);
	}

	private Page<UserEvent> fetchUserEventsByMonth(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
		return userEventRepository.findUserEventsByMonth(userId, start, end, pageable);
	}

	private Page<EventResponseDto> convertToEventResponsePage(Page<UserEvent> userEventPage) {
		return userEventPage.map(UserEventConverter::toResponse);
	}
}