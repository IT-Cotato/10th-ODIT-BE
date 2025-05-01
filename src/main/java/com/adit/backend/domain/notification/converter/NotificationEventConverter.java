package com.adit.backend.domain.notification.converter;

import static com.adit.backend.domain.notification.enums.NotificationType.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.notification.enums.NotificationType;
import com.adit.backend.domain.notification.event.NotificationEvent;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.domain.user.entity.Friendship;
import com.adit.backend.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationEventConverter {

	private NotificationEvent createNotificationEvent(String userEmail, NotificationType type, String content) {
		return NotificationEvent.builder()
			.userEmail(userEmail)
			.category(type.getCategory())
			.notificationType(type)
			.content(content)
			.build();
	}

	/*ANNOUNCEMENT : Not Defined*/

	/* PLACE_UNVISITED */
	public NotificationEvent toUnvisitedEvent(UserPlace userPlace) {
		String content = String.format("아직 '%s'에 방문하지 않으셨나요?", userPlace.getPlace().getPlaceName());
		return createNotificationEvent(userPlace.getUser().getEmail(), PLACE_UNVISITED, content);
	}

	/* EVENT_START_SOON */
	public NotificationEvent toStartEvent(UserEvent userEvent, int remainingDays) {
		String content = String.format("'%s'이 %d일 뒤 시작해요!", userEvent.getName(), remainingDays);
		return createNotificationEvent(userEvent.getUser().getEmail(), EVENT_START_SOON, content);
	}

	/* EVENT_DURATION_MISSING */
	public NotificationEvent toMissingEvent(UserEvent userEvent) {
		String content = String.format("'%s' 기간을 아직 입력하지 않았어요!", userEvent.getName());
		return createNotificationEvent(userEvent.getUser().getEmail(), EVENT_DURATION_MISSING, content);
	}

	/* EVENT_DURATION_MISSING - 복수 이벤트 옵션 */
	public NotificationEvent toMultipleMissingEvent(List<UserEvent> userEvents) {
		if (userEvents == null || userEvents.isEmpty()) {
			throw new IllegalArgumentException("이벤트 리스트에 하나 이상의 요소가 존재해야 합니다.");
		}
		String primaryEventName = userEvents.get(0).getName();
		int remainingCount = userEvents.size() - 1;
		String content = String.format("'%s'외 %d개의 이벤트 기간을 아직 입력하지 않았어요!", primaryEventName, remainingCount);
		return createNotificationEvent(userEvents.get(0).getUser().getEmail(), EVENT_DURATION_MISSING, content);
	}

	/* FRIEND_REQUEST_RECEIVED */
	public NotificationEvent toRequestEvent(Friendship friendship) {
		log.info("친구 요청 알림 생성");
		String content = String.format("%s님이 친구 맺기를 요청했어요!", friendship.getFromUser().getNickname());
		return createNotificationEvent(friendship.getToUser().getEmail(), FRIEND_REQUEST_RECEIVED, content);
	}

	/* FRIEND_REQUEST_ACCEPTED */
	public NotificationEvent toAcceptEvent(Friendship friendship) {
		String content = String.format("%s님이 친구 맺기를 수락했어요!", friendship.getToUser().getNickname());
		return createNotificationEvent(friendship.getFromUser().getEmail(), FRIEND_REQUEST_ACCEPTED, content);
	}

	/* FRIEND_SAVED_MY_PLACE */
	public NotificationEvent toSavedEvent(UserPlace friendPlace, User user) {
		String content = String.format("%s님도 '%s'를 저장했어요!", friendPlace.getUser().getNickname(),
			friendPlace.getPlace().getPlaceName());
		return createNotificationEvent(user.getEmail(), FRIEND_SAVED_MY_PLACE, content);
	}
}
