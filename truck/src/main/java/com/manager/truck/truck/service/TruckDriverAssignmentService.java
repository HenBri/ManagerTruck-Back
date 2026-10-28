package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TruckDriverAssignmentService {

    TruckDriverAssignment save(TruckDriverAssignment assignment);

    TruckDriverAssignment update(TruckDriverAssignment assignment);

    TruckDriverAssignment findById(Long assignmentId);

    Optional<TruckDriverAssignment> findOptionalById(Long assignmentId);

    Page<TruckDriverAssignment> findByTruckId(Long truckId, Pageable pageable);

    Page<TruckDriverAssignment> findByDriverId(Long driverId, Pageable pageable);

    Page<TruckDriverAssignment> findAll(Pageable pageable);
}
