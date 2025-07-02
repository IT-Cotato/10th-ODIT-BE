package com.odit.backend.domain.event.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.odit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.odit.backend.domain.image.entity.UserEventImage;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_event")
public class UserEvent extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private Event event;

	@Column(name = "custom_start_date")
	private LocalDateTime customStartDate;

	@Column(name = "custom_end_date")
	private LocalDateTime customEndDate;

	private String memo;

	private Boolean visited;

	@OneToMany(mappedBy = "userEvent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserEventImage> images = new ArrayList<>();

	// 팩토리 메서드
	public static UserEvent createEvent(LocalDateTime startDate, LocalDateTime endDate,
		String memo, Boolean visited) {
		UserEvent userEvent = new UserEvent();
		userEvent.customStartDate = startDate;
		userEvent.customEndDate = endDate;
		userEvent.memo = memo;
		userEvent.visited = visited;
		return userEvent;
	}

	// 연관관계 메서드
	public void addImage(UserEventImage image) {
		this.images.add(image);
		image.assignEvent(this);
	}

	public void assignEvent(Event event) {
		this.event = event;
	}

	public void assignUser(User user) {
		this.user = user;
	}

	// 업데이트 메서드
	public void updateEvent(EventUpdateRequestDto request) {
		if (request.startDate() != null)
			this.customStartDate = request.startDate();
		if (request.endDate() != null)
			this.customEndDate = request.endDate();
		if (request.memo() != null)
			this.memo = request.memo();
		if (request.visited() != null)
			this.visited = request.visited();
	}

	public void updateMemo(String memo) {
		this.memo = memo;
	}
}
