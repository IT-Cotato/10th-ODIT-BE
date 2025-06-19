package com.odit.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odit.backend.domain.image.entity.EventImage;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
}