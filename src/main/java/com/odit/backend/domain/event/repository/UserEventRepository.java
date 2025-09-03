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

	@Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.event e LEFT JOIN FETCH e.images WHERE DATE(e.startDate) = :date")
	List<UserEvent> findByDate(@Param("date") LocalDate date);

	@Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.event e LEFT JOIN FETCH e.images WHERE e.startDate >= :startDateTime AND e.startDate < :endDateTime")
	List<UserEvent> findByDateRange(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

	@Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.event e LEFT JOIN FETCH e.images ORDER BY ue.visited DESC")
	List<UserEvent> findPopularEvents();

	@Query("SELECT DISTINCT ue FROM UserEvent ue JOIN FETCH ue.user JOIN FETCH ue.event e LEFT JOIN FETCH e.images WHERE ue.user.id = :userId")
	List<UserEvent> findAllUserEvents(@Param("userId") Long userId);

	@Query(
		value = "SELECT DISTINCT ue FROM UserEvent ue JOIN FETCH ue.user u JOIN FETCH ue.event e " +
			"WHERE u.id = :userId AND e.startDate >= :start AND e.startDate < :end " +
			"ORDER BY e.startDate DESC",
		countQuery = "SELECT COUNT(ue) FROM UserEvent ue JOIN ue.user u JOIN ue.event e " +
			"WHERE u.id = :userId AND e.startDate >= :start AND e.startDate < :end"
	)
	Page<UserEvent> findUserEventsByMonth(
		@Param("userId") Long userId, 
		@Param("start") LocalDateTime start, 
		@Param("end") LocalDateTime end, 
		Pageable pageable
	);

	@Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.event e LEFT JOIN FETCH e.images WHERE ue.id = :eventId AND ue.user.id = :userId")
	Optional<UserEvent> findByIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

	@Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.event e LEFT JOIN FETCH e.images WHERE ue.id = :id")
	Optional<UserEvent> findByIdWithEvent(@Param("id") Long id);

	@Query("SELECT distinct ue FROM UserEvent ue JOIN FETCH ue.event e LEFT JOIN FETCH e.images")
	List<UserEvent> findAllWithEvent();

}