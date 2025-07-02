package com.odit.backend.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.odit.backend.domain.place.entity.PlaceStatistics;

@Repository
public interface PlaceStatisticsRepository extends JpaRepository<PlaceStatistics, Long> {

	@Query("SELECT ps FROM PlaceStatistics ps where ps.place.id = :id")
	Optional<PlaceStatistics> findByPlaceId(@Param("id") Long id);


}
