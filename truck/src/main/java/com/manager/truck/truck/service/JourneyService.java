package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface JourneyService {

    Journey save(Journey journey);

    Journey update(Journey journey);

    Journey findById(Long journeyId);

    Optional<Journey> findOptionalById(Long journeyId);

    Optional<Journey> findByFreightId(Long freightId);

    Page<Journey> findByAssignmentId(Long assignmentId, Pageable pageable);

    Page<Journey> findByState(JourneyState state, Pageable pageable);

    Page<Journey> findAll(Pageable pageable);
}
