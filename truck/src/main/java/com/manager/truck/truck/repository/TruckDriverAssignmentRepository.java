package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TruckDriverAssignmentRepository extends JpaRepository<TruckDriverAssignment, Long> {

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.assignmentId = :assignmentId AND a.isDelete = false")
    Optional<TruckDriverAssignment> findActiveById(@Param("assignmentId") Long assignmentId);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.truckId = :truckId AND a.isDelete = false")
    Page<TruckDriverAssignment> findByTruckId(@Param("truckId") Long truckId, Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.driverId = :driverId AND a.isDelete = false")
    Page<TruckDriverAssignment> findByDriverId(@Param("driverId") Long driverId, Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.isDelete = false")
    Page<TruckDriverAssignment> findAllActive(Pageable pageable);
}
