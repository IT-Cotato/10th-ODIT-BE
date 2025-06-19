package com.odit.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odit.backend.domain.image.entity.UserPlaceImage;

public interface UserPlaceImageRepository extends JpaRepository<UserPlaceImage, Long> {
}