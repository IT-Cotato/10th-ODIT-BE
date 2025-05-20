package com.adit.backend.domain.image.service.command;

import static com.adit.backend.domain.image.enums.Directory.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.event.dto.request.EventRequestDto;
import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.image.entity.EventImage;
import com.adit.backend.domain.image.repository.EventImageRepository;
import com.adit.backend.infra.s3.service.AwsS3Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImageCommandService {
	private final AwsS3Service s3Service;
	private final EventImageRepository eventImageRepository;

	public void addImageToEvent(EventRequestDto request, Event event) {
		List<EventImage> imageList = s3Service.uploadFile(request.imageUrlList(), EVENT.getPath())
			.join()
			.stream()
			.map(url -> EventImage.builder().url(url).build())
			.toList();
		imageList.forEach(event::addImage);
		eventImageRepository.saveAll(imageList);
	}
}
