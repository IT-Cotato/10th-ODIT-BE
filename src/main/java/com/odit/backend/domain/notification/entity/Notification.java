package com.odit.backend.domain.notification.entity;

import org.hibernate.annotations.ColumnDefault;

import com.odit.backend.domain.notification.enums.NotificationType;
import com.odit.backend.domain.user.entity.User;
import com.odit.backend.global.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String message;

	@NotNull
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@NotBlank
	private String category;

	@NotNull
	@ColumnDefault("false")
	private boolean isRead = false;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Notification(String message, NotificationType notificationType, String category, boolean isRead) {
		this.message = message;
		this.notificationType = notificationType;
		this.category = category;
		this.isRead = isRead;
	}

	//연관관계 메서드
	public void assignUser(User user) {
		this.user = user;
	}

	public void markAsRead() {
		this.isRead = true;
	}
}
