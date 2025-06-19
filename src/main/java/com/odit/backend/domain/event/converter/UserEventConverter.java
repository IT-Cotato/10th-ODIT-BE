package com.odit.backend.domain.event.converter;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.image.entity.UserEventImage;

@Component
public class UserEventConverter {

	public UserEvent toEntity(EventRequestDto request) {
		return UserEvent.createEvent(
			request.startDate(),
			request.endDate(),
			request.memo(),
			false
		);
	}

	public EventResponseDto toResponse(UserEvent userEvent) {
		return EventResponseDto.builder()
			.id(userEvent.getId())
			.startDate(userEvent.getCustomStartDate())
			.endDate(userEvent.getCustomEndDate())
			.memo(userEvent.getMemo())
			.visited(userEvent.getVisited())
			.imageUrlList(Optional.ofNullable(userEvent.getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(UserEventImage::getUrl)
				.toList())
			.build();
	}

	public void updateEntity(UserEvent userEvent, EventUpdateRequestDto updateRequest) {
		userEvent.updateEvent(updateRequest);  // Event 엔터티의 update 메서드 호출
	}
}