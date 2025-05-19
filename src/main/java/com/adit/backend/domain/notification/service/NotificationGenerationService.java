package com.adit.backend.domain.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.adit.backend.domain.notification.converter.NotificationEventConverter;
import com.adit.backend.domain.notification.event.NotificationEvent;
import com.adit.backend.domain.notification.service.command.NotificationCommandService;
import com.adit.backend.domain.place.entity.Place;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.domain.place.service.query.UserPlaceQueryService;
import com.adit.backend.domain.user.entity.Friendship;
import com.adit.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public class NotificationGenerationService {

	private final NotificationEventConverter notificationEventConverter;
	private final NotificationCommandService notificationCommandService;
	private final UserPlaceQueryService userPlaceQueryService;

	@Async
	public void createNotificationOfAFriendRequest(Friendship friendship) {
		NotificationEvent event = notificationEventConverter.toRequestEvent(friendship);
		notificationCommandService.sendNotification(event);
	}

	@Async
	public void createNotificationOfAFriendAccept(Friendship friendship) {
		NotificationEvent event = notificationEventConverter.toAcceptEvent(friendship);
		notificationCommandService.sendNotification(event);
	}

	@Async
	public void createNotificationOfASavedPlace(User user, Place place, UserPlace userPlace) {
		userPlaceQueryService.findRelatedUserPlace(user, place)
			.stream()
			.map(friendUserPlace -> notificationEventConverter.toSavedEvent(userPlace, friendUserPlace.getUser()))
			.forEach(notificationCommandService::sendNotification);
	}
}
