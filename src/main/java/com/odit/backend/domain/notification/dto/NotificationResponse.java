package com.odit.backend.domain.notification.dto;

import com.odit.backend.domain.notification.enums.NotificationType;

import lombok.Builder;

@Builder
public record NotificationResponse(String message,
								   String category,
								   NotificationType notificationType,
								   String  createdAt,
								   boolean isRead) {
}

