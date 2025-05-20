package com.adit.backend.domain.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
  Optional<Event> findByName(String name);
}