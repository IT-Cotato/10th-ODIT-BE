package com.adit.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.image.entity.UserPlaceImage;

public interface UserPlaceImageRepository extends JpaRepository<UserPlaceImage, Long> {
}