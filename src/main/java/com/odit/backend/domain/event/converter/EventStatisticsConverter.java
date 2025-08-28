package com.odit.backend.domain.event.converter;

import com.odit.backend.domain.event.entity.EventStatistics;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventStatisticsConverter {

	public EventStatistics toEntity() {
		return EventStatistics.builder().build();
	}
}
