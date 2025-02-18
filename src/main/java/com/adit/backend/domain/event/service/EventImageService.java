package com.adit.backend.domain.event.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.adit.backend.domain.event.entity.UserEvent;
import com.adit.backend.domain.event.exception.EventException;
import com.adit.backend.domain.event.repository.UserEventRepository;
import com.adit.backend.domain.image.dto.request.ImageUpdateRequestDto;
import com.adit.backend.domain.image.dto.response.ImageResponseDto;
import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.domain.image.enums.Directory;
import com.adit.backend.domain.image.repository.ImageRepository;
import com.adit.backend.domain.image.service.command.ImageCommandService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImageService {

    private final ImageCommandService imageCommandService;
    private final UserEventRepository userEventRepository;
    private final ImageRepository imageRepository;

    /**
     * 이벤트에 새로운 이미지 추가
     */
    public List<ImageResponseDto> addEventImages(Long eventId, List<MultipartFile> images) {
        UserEvent userEvent = userEventRepository.findById(eventId)
            .orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

        List<Image> uploadedImages = imageCommandService.uploadImages(images, Directory.EVENT.getPath());

        // 이미지 연관관계 설정
        uploadedImages.forEach(userEvent::addImage);
        userEventRepository.save(userEvent);

        return uploadedImages.stream()
            .map(imageCommandService::toResponse)
            .toList();

    }

    /**
     * 기존 이벤트 이미지 업데이트 (기존 이미지 교체)
     */
    public List<ImageResponseDto> updateEventImages(Long eventId, List<ImageUpdateRequestDto> imageUpdateRequests) {
        UserEvent userEvent = userEventRepository.findById(eventId)
            .orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

        return imageUpdateRequests.stream()
            .map(req -> imageCommandService.updateImage(req.imageId(), req.newImage()))
            .toList();
    }

    /**
     * 이벤트 이미지 삭제 (DB에서도 삭제)
     */
    public void deleteEventImage(Long eventId, Long imageId) {
        UserEvent userEvent = userEventRepository.findById(eventId)
            .orElseThrow(() -> new EventException(EVENT_NOT_FOUND));

        //삭제할 이미지 찾기
        Image image = userEvent.getImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new EventException(IMAGE_NOT_FOUND));

      try {
          // 1. S3에서 이미지 삭제
          imageCommandService.deleteImage(image.getId());

          // 2. UserEvent에서 이미지 제거 (메모리 상 제거)
          userEvent.getImages().remove(image);

          // 3. DB에서 이미지 삭제
          imageRepository.delete(image);
          log.info("[이벤트 이미지 삭제 완료] eventId = {}, imageId = {}", eventId, imageId);

      } catch (Exception e) {
          log.error("[이벤트 이미지 삭제 실패] eventId = {}, imageId = {}, 이유: {}", eventId, imageId, e.getMessage(), e);
          throw new EventException(IMAGE_DELETE_FAILED);
      }
    }
}
