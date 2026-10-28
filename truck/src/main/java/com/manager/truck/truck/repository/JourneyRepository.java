package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {

    @Query("SELECT j FROM Journey j WHERE j.journeyId = :journeyId AND j.isDelete = false")
    Optional<Journey> findActiveById(@Param("journeyId") Long journeyId);

    @Query("SELECT j FROM Journey j WHERE j.freightId = :freightId AND j.isDelete = false")
    Optional<Journey> findByFreightId(@Param("freightId") Long freightId);

    @Query("SELECT j FROM Journey j WHERE j.assignmentId = :assignmentId AND j.isDelete = false")
    Page<Journey> findByAssignmentId(@Param("assignmentId") Long assignmentId, Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.state = :state AND j.isDelete = false")
    Page<Journey> findByState(@Param("state") JourneyState state, Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.isDelete = false")
    Page<Journey> findAllActive(Pageable pageable);
}
