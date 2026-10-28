package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JourneyServiceImpl implements JourneyService {

    private final JourneyRepository journeyRepository;

    @Override
    public Journey save(Journey journey) {
        return journeyRepository.save(journey);
    }

    @Override
    public Journey update(Journey journey) {
        return journeyRepository.save(journey);
    }

    @Override
    public Journey findById(Long journeyId) {
        return journeyRepository.findActiveById(journeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Viaje no encontrado con el id " + journeyId));
    }

    @Override
    public Optional<Journey> findOptionalById(Long journeyId) {
        return journeyRepository.findActiveById(journeyId);
    }

    @Override
    public Optional<Journey> findByFreightId(Long freightId) {
        return journeyRepository.findByFreightId(freightId);
    }

    @Override
    public Page<Journey> findByAssignmentId(Long assignmentId, Pageable pageable) {
        return journeyRepository.findByAssignmentId(assignmentId, pageable);
    }

    @Override
    public Page<Journey> findByState(JourneyState state, Pageable pageable) {
        return journeyRepository.findByState(state, pageable);
    }

    @Override
    public Page<Journey> findAll(Pageable pageable) {
        return journeyRepository.findAllActive(pageable);
    }
}
