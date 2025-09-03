package com.odit.backend.domain.event.converter;

import java.util.Collections;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.dto.request.MonthlyEventRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.dto.response.MonthlyEventPageResponseDto;
import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.image.entity.EventImage;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventConverter {

	public Event toEntity(EventRequestDto request) {
		return Event.builder()
			.seq(request.seq())
			.title(request.title())
			.category(request.category())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.build();
	}

	public EventResponseDto toResponse(Event event) {
		return EventResponseDto.builder()
			.id(event.getId())
			.title(event.getTitle())
			.category(event.getCategory())
			.startDate(event.getStartDate())
			.endDate(event.getEndDate())
			.imageUrlList(Optional.ofNullable(event.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(EventImage::getUrl)
				.toList())
			.seq(event.getSeq())
			.build();
	}

	public MonthlyEventRequestDto toMonthlyRequest(Integer year, Integer month) {
		return MonthlyEventRequestDto.builder()
			.month(month)
			.year(year)
			.build();
	}

	public static MonthlyEventPageResponseDto toMonthlyEventPageResponseDto(Page<EventResponseDto> page, Integer year,
		Integer month) {
		return MonthlyEventPageResponseDto.builder()
			.content(page.getContent())
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.first(page.isFirst())
			.last(page.isLast())
			.empty(page.isEmpty())
			.year(year)
			.month(month)
			.build();
	}
}