package com.odit.backend.domain.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.odit.backend.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	@Query("SELECT ps.place.id FROM PlaceStatistics ps ORDER BY ps.bookmarkCount DESC")
	List<Long> findByPopular(Pageable pageable);

	@Query("SELECT p FROM Place p where p.placeName LIKE %:placeName%")
	Optional<Place> findByBusinessName(@Param("placeName") String businessName);

	@Query("SELECT p FROM Place p where p.seq = :seq")
	Optional<Place> findBySequence(@Param("seq") Long sequence);
}