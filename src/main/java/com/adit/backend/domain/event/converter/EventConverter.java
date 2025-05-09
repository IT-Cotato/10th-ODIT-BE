package com.adit.backend.domain.event.converter;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.adit.backend.domain.event.dto.request.EventRequestDto;
import com.adit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.adit.backend.domain.event.dto.response.EventResponseDto;
import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.image.entity.Image;

@Component
public class EventConverter {

	public Event toEntity(EventRequestDto request) {
		return Event.createEvent(
			request.name(),
			request.category(),
			request.startDate(),
			request.endDate(),
			request.memo()
		);
	}

	public EventResponseDto toResponse(Event event) {
		return EventResponseDto.builder()
			.id(event.getId())
			.name(event.getName())
			.category(event.getCategory())
			.startDate(event.getStartDate())
			.endDate(event.getEndDate())
			.memo(event.getMemo())
			.imageUrlList(Optional.ofNullable(event.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(Image::getUrl)
				.collect(Collectors.toList()))
			.build();
	}

	public void updateEntity(Event event, EventUpdateRequestDto updateRequest) {
		event.updateEvent(updateRequest);  // Event 엔터티의 update 메서드 호출
	}
}