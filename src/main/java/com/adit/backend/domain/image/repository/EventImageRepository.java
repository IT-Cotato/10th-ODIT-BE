package com.adit.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.image.entity.EventImage;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
}