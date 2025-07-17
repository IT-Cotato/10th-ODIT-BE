package com.odit.backend.domain.event.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.odit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.odit.backend.domain.image.entity.EventImage;
import com.odit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event")
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Column(unique = true)
	private Long externalId;

	@NonNull
	private String title;

	@NonNull
	private String category;

	@NonNull
	private LocalDateTime startDate;

	@NonNull
	@Column(name = "end_date")
	private LocalDateTime endDate;

	@Builder.Default
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	List<UserEvent> userEvents = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventImage> images = new ArrayList<>();

	public static Event createEvent(Long externalId, String name, String category, LocalDateTime startDate,
		LocalDateTime endDate) {
		Event event = new Event();
		event.externalId = externalId;
		event.title = name;
		event.category = category;
		event.startDate = startDate;
		event.endDate = endDate;
		return event;
	}

	//연관관계 메서드
	public void addUserEvent(UserEvent userEvent) {
		this.userEvents.add(userEvent);
		userEvent.assignEvent(this);
	}

	public void addImage(EventImage image) {
		this.images.add(image);
		image.assignEvent(this);
	}

	public void updateEvent(EventUpdateRequestDto updateRequest) {
		if (updateRequest.name() != null)
			this.title = updateRequest.name();
		if (updateRequest.category() != null)
			this.category = updateRequest.category();
		if (updateRequest.startDate() != null)
			this.startDate = updateRequest.startDate();
		if (updateRequest.endDate() != null)
			this.endDate = updateRequest.endDate();
	}
}