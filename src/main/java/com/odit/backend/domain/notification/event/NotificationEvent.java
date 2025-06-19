package com.odit.backend.domain.notification.event;

import java.time.LocalDateTime;

import com.odit.backend.domain.notification.enums.NotificationType;

import lombok.Builder;

@Builder
public record NotificationEvent(
	String userEmail,
	String content,
	String category,
	NotificationType notificationType,
	LocalDateTime createdAt
) {
}
