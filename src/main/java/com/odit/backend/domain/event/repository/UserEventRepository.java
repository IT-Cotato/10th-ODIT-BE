package com.odit.backend.domain.event.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.odit.backend.domain.event.entity.UserEvent;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

	@Query("SELECT ue FROM UserEvent ue WHERE DATE(ue.event.startDate) = :date")
	List<UserEvent> findByDate(@Param("date") LocalDate date);

	@Query("SELECT ue FROM UserEvent ue ORDER BY ue.visited DESC")
	List<UserEvent> findPopularEvents();

	@Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.user JOIN FETCH ue.event WHERE ue.user.id = :userId")
	List<UserEvent> findAllUserEvents(@Param("userId") Long userId);

	@Query(
		value = "SELECT ue FROM UserEvent ue JOIN FETCH ue.user JOIN FETCH ue.event " +
			"WHERE ue.user.id = :userId AND ue.event.startDate >= :start AND ue.event.startDate < :end",
		countQuery = "SELECT COUNT(ue) FROM UserEvent ue " +
			"WHERE ue.user.id = :userId AND ue.event.startDate >= :start AND ue.event.startDate < :end"
	)
	Page<UserEvent> findUserEventsByMonth(
		@Param("userId") Long userId, 
		@Param("start") LocalDateTime start, 
		@Param("end") LocalDateTime end, 
		Pageable pageable
	);

	@Query("SELECT ue FROM UserEvent ue WHERE ue.id = :eventId AND ue.user.id = :userId")
	Optional<UserEvent> findByIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

}