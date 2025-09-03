package com.odit.backend.domain.event.entity;

import com.odit.backend.domain.event.exception.EventException;
import com.odit.backend.global.entity.BaseEntity;
import com.odit.backend.global.error.GlobalErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
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
public class EventStatistics extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Version
	private Long version;

	@Builder.Default
	@Column(nullable = false)
	private Integer bookmarkCount = 0; // 기본값: 0

	@Builder.Default
	@Column(nullable = false)
	private Integer visitCount = 0; // 기본값: 0

	// bookmarkCount 증가 메서드 추가
	public void incrementBookmarkCount() {
		this.bookmarkCount++;
	}

	// visitCount 증가 메서드 추가
	public void incrementVisitCount() {
		this.visitCount++;
	}

	public void decrementBookmarkCount() {
		if (bookmarkCount > 0) {
			this.bookmarkCount = Math.max(0, bookmarkCount - 1);
		}
	}

	public void decrementVisitCount() {
		if (visitCount > 0) {
			this.visitCount = Math.max(0, visitCount - 1);
		}
	}

	public void assignEvent(Event event) {
		if (event == null) {
			throw new EventException(GlobalErrorCode.EVENT_NOT_FOUND);
		}
		this.event = event;
	}
}