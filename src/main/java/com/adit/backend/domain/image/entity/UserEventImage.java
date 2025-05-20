package com.adit.backend.domain.image.entity;

import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "user_event_image")
public class UserEventImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_event_id", nullable = false)
	private UserEvent userEvent;

	@Column(nullable = false)
	private String url;

	public void assignEvent(UserEvent userEvent) {
		this.userEvent = userEvent;
	}

	public void updateUrl(String newImageUrl) {
		this.url = newImageUrl;
	}
}
