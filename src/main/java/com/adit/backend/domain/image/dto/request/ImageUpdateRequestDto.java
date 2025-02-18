package com.adit.backend.domain.image.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record ImageUpdateRequestDto(
    @NotNull(message = "이미지 ID는 필수입니다.") Long imageId,
    @NotNull(message = "새 이미지 파일은 필수입니다.") MultipartFile newImage
) {
}
