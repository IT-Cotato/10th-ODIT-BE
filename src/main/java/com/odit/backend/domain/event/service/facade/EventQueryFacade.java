package com.odit.backend.domain.event.service.facade;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.event.dto.request.MonthlyEventRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.dto.response.MonthlyEventPageResponseDto;
import com.odit.backend.domain.event.entity.EventStatistics;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.event.service.query.UserEventQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventQueryFacade {

	private final UserEventQueryService userEventQueryService;

	public MonthlyEventPageResponseDto getUserEventsByMonth(Long userId, MonthlyEventRequestDto request, Pageable pageable) {
		return userEventQueryService.getUserEventsByMonth(userId, request.year(), request.month(), pageable);
	}
}
