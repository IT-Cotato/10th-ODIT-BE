package com.odit.backend.domain.event.converter;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.entity.Event;
import com.odit.backend.domain.image.entity.EventImage;

@Component
public class EventConverter {

	public Event toEntity(EventRequestDto request) {
		return Event.createEvent(
			request.externalId(),
			request.name(),
			request.category(),
			request.startDate(),
			request.endDate()
		);
	}

	public EventResponseDto toResponse(Event event) {
		return EventResponseDto.builder()
			.id(event.getId())
			.name(event.getName())
			.category(event.getCategory())
			.startDate(event.getStartDate())
			.endDate(event.getEndDate())
			.imageUrlList(Optional.ofNullable(event.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(EventImage::getUrl)
				.toList())
			.build();
	}

	public void updateEntity(Event event, EventUpdateRequestDto updateRequest) {
		event.updateEvent(updateRequest);  // Event 엔터티의 update 메서드 호출
	}
}