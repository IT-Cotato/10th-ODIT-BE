package com.odit.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odit.backend.domain.image.entity.PlaceImage;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
}