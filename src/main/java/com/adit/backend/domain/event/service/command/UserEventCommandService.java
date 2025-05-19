package com.adit.backend.domain.event.service.command;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.event.converter.UserEventConverter;
import com.adit.backend.domain.event.dto.request.EventRequestDto;
import com.adit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.adit.backend.domain.event.dto.response.EventResponseDto;
import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.event.exception.EventException;
import com.adit.backend.domain.event.repository.UserEventRepository;
import com.adit.backend.domain.image.entity.UserEventImage;
import com.adit.backend.domain.image.service.command.UserEventImageCommandService;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.domain.user.service.query.UserQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEventCommandService {

	private final UserEventRepository userEventRepository;
	private final UserEventConverter userEventConverter;
	private final UserQueryService userQueryService;
	private final EventCommandService eventCommandService;
	private final UserEventImageCommandService userEventImageCommandService;

	/**
	 * 새로운 이벤트 생성
	 */
	public EventResponseDto createUserEvent(EventRequestDto request, Long userId) {
		User user = userQueryService.findUserById(userId);
		Event event = eventCommandService.saveOrFindEvent(request);
		UserEvent userEvent = userEventConverter.toEntity(request);

		saveUserEventRelation(event, userEvent, user);

		return userEventConverter.toResponse(userEvent);
	}

	private void saveUserEventRelation(Event event, UserEvent userEvent, User user) {
		event.addUserEvent(userEvent);
		user.addUserEvent(userEvent);
		userEventRepository.save(userEvent);
	}

	/**
	 * 이벤트 기본 정보 수정 (이미지 제외)
	 */
	public EventResponseDto updateEventInfo(Long id, EventUpdateRequestDto request) {
		UserEvent userEvent = userEventRepository.findById(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

		userEventConverter.updateEntity(userEvent, request);
		userEventRepository.save(userEvent);

		return userEventConverter.toResponse(userEvent);
	}

	/**
	 * 이벤트 메모 수정
	 */
	public EventResponseDto updateUserEventMemo(Long id, String memo) {
		UserEvent userEvent = userEventRepository.findById(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));
		userEvent.updateMemo(memo);
		return userEventConverter.toResponse(userEvent);
	}

	/**
	 * 이벤트 삭제 (연관된 이미지도 삭제, 트랜잭션 보장)
	 */
	public void deleteEvent(Long id) {
		UserEvent userEvent = userEventRepository.findById(id)
			.orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

		try {
			// 1. 이미지 삭제 (모든 이미지 삭제 성공 시 이벤트 삭제 진행)
			for (UserEventImage image : userEvent.getImages()) {
				userEventImageCommandService.deleteImage(image.getId());
			}

			// 2. 이벤트 삭제
			userEventRepository.delete(userEvent);
			log.info("[이벤트 삭제 완료] eventId = {}", id);

		} catch (Exception e) {
			log.error("[이벤트 삭제 실패] eventId = {}, 이유: {}", id, e.getMessage(), e);
			throw new EventException(EVENT_DELETE_FAILED); // 실패 시 예외 발생 → 트랜잭션 롤백
		}
	}

}
