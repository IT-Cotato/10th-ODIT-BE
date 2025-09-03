package com.odit.backend.domain.event.converter;

import java.util.Collections;
import java.util.Optional;

import com.odit.backend.domain.event.dto.request.EventRequestDto;
import com.odit.backend.domain.event.dto.response.EventResponseDto;
import com.odit.backend.domain.event.entity.UserEvent;
import com.odit.backend.domain.image.entity.EventImage;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserEventConverter {

	public UserEvent toEntity(EventRequestDto request) {
		return UserEvent.createEvent(
			request.memo(),
			false
		);
	}

	public EventResponseDto toResponse(UserEvent userEvent) {
		return EventResponseDto.builder()
			.id(userEvent.getId())
			.seq(userEvent.getEvent().getSeq())
			.title(userEvent.getEvent().getTitle())
			.startDate(userEvent.getEvent().getStartDate())
			.endDate(userEvent.getEvent().getEndDate())
			.category(userEvent.getEvent().getCategory())
			.memo(userEvent.getMemo())
			.visited(userEvent.getVisited())
			.imageUrlList(Optional.ofNullable(userEvent.getEvent().getImages())
				.orElse(Collections.emptyList())
				.stream()
				.map(EventImage::getUrl)
				.toList())
			.build();
	}
}