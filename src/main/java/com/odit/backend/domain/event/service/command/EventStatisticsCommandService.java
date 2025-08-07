package com.odit.backend.domain.event.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.event.entity.EventStatistics;
import com.odit.backend.domain.event.repository.EventStatisticsRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventStatisticsCommandService {
	private final EventStatisticsRepository eventStatisticsRepository;

	public EventStatistics save(EventStatistics eventStatistics) {
		return eventStatisticsRepository.save(eventStatistics);
	}
	
	public void delete(Long eventId) {
		eventStatisticsRepository.deleteById(eventId);
	}
}
