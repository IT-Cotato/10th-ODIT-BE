package com.odit.backend.domain.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odit.backend.domain.image.entity.UserEventImage;

public interface UserEventImageRepository extends JpaRepository<UserEventImage, Long> {
	Optional<UserEventImage> findById(Long id);
}