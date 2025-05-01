package com.adit.backend.domain.image.entity;

import com.adit.backend.domain.event.entity.CommonEvent;
import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.place.entity.Place;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Image extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_place_id")
	private UserPlace userPlace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_event_id")
	private UserEvent userEvent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "common_event_id")
	private CommonEvent commonEvent;

	@Column(nullable = false)
	private String url;

	//연관관계 메서드
	public void assignEvent(UserEvent userEvent) {
		this.userEvent = userEvent;
	}

	public void assignPlace(Place place) {
		this.place = place;
	}

	public void assignUserPlace(UserPlace userPlace) {
		this.userPlace = userPlace;
	}

	public void assignCommonEvent(CommonEvent commonEvent) {
		this.commonEvent = commonEvent;
	}

	public void updateUrl(String newImageUrl) {
		this.url = newImageUrl;
	}
}