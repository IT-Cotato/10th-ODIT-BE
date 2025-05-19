package com.adit.backend.domain.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.image.entity.UserEventImage;

public interface UserEventImageRepository extends JpaRepository<UserEventImage, Long> {
	Optional<UserEventImage> findById(Long id);
}