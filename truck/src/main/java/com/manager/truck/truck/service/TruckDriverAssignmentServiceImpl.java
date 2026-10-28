package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.TruckDriverAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TruckDriverAssignmentServiceImpl implements TruckDriverAssignmentService {

    private final TruckDriverAssignmentRepository assignmentRepository;

    @Override
    public TruckDriverAssignment save(TruckDriverAssignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    public TruckDriverAssignment update(TruckDriverAssignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    public TruckDriverAssignment findById(Long assignmentId) {
        return assignmentRepository.findActiveById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con el id " + assignmentId));
    }

    @Override
    public Optional<TruckDriverAssignment> findOptionalById(Long assignmentId) {
        return assignmentRepository.findActiveById(assignmentId);
    }

    @Override
    public Page<TruckDriverAssignment> findByTruckId(Long truckId, Pageable pageable) {
        return assignmentRepository.findByTruckId(truckId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByDriverId(Long driverId, Pageable pageable) {
        return assignmentRepository.findByDriverId(driverId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findAll(Pageable pageable) {
        return assignmentRepository.findAllActive(pageable);
    }
}
