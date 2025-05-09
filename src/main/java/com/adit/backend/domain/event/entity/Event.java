package com.adit.backend.domain.event.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.adit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.global.entity.BaseEntity;

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

	@Column(unique = true, nullable = false)
	private String name;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false, name = "start_date")
	private LocalDateTime startDate;

	@Column(nullable = false, name = "end_date")
	private LocalDateTime endDate;

	@Builder.Default
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	List<UserEvent> userEvents = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	public static Event createEvent(String name, String category, LocalDateTime startDate, LocalDateTime endDate) {
		Event event = new Event();
		event.name = name;
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

	public void addImage(Image image) {
		this.images.add(image);
		image.assignEvent(this);
	}

	public void updateEvent(EventUpdateRequestDto updateRequest) {
		if (updateRequest.name() != null)
			this.name = updateRequest.name();
		if (updateRequest.category() != null)
			this.category = updateRequest.category();
		if (updateRequest.startDate() != null)
			this.startDate = updateRequest.startDate();
		if (updateRequest.endDate() != null)
			this.endDate = updateRequest.endDate();
	}
}