package com.adit.backend.domain.event.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adit.backend.domain.event.entity.UserEvent;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

	@Query("SELECT ue FROM UserEvent ue WHERE DATE(ue.customStartDate) = :date")
	List<UserEvent> findByDate(@Param("date") LocalDate date);

	@Query("SELECT ue FROM UserEvent ue WHERE ue.customStartDate IS NULL AND ue.customEndDate IS NULL")
	List<UserEvent> findNoDateEvents();

	@Query("SELECT ue FROM UserEvent ue ORDER BY ue.visited DESC")
	List<UserEvent> findPopularEvents();
}