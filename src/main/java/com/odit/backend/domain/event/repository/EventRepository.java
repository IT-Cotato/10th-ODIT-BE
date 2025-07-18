package com.odit.backend.domain.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odit.backend.domain.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
  Optional<Event> findByTitle(String title);

  Optional<Event> findBySeq(Long seq);
}