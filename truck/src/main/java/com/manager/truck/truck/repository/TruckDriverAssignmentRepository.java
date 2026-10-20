package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TruckDriverAssignmentRepository extends JpaRepository<TruckDriverAssignment, Long> {

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.companyId = :companyId AND a.assignmentId = :assignmentId")
    Optional<TruckDriverAssignment> findByCompanyIdAndAssignmentId(@Param("companyId") Long companyId,
                                                                   @Param("assignmentId") Long assignmentId);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.companyId = :companyId")
    Page<TruckDriverAssignment> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.companyId = :companyId AND a.truckId = :truckId")
    Page<TruckDriverAssignment> findByCompanyIdAndTruckId(@Param("companyId") Long companyId,
                                                          @Param("truckId") Long truckId,
                                                          Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.companyId = :companyId AND a.driverId = :driverId")
    Page<TruckDriverAssignment> findByCompanyIdAndDriverId(@Param("companyId") Long companyId,
                                                           @Param("driverId") Long driverId,
                                                           Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a WHERE a.companyId = :companyId "
            + "AND a.truckId = :truckId AND a.driverId = :driverId")
    Page<TruckDriverAssignment> findByCompanyIdAndTruckIdAndDriverId(@Param("companyId") Long companyId,
                                                                     @Param("truckId") Long truckId,
                                                                     @Param("driverId") Long driverId,
                                                                     Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a, Truck t WHERE a.companyId = t.companyId "
            + "AND a.truckId = t.truckId AND a.companyId = :companyId AND t.ownerPartnerId = :ownerPartnerId")
    Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerId(@Param("companyId") Long companyId,
                                                                 @Param("ownerPartnerId") Long ownerPartnerId,
                                                                 Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a, Truck t WHERE a.companyId = t.companyId "
            + "AND a.truckId = t.truckId AND a.companyId = :companyId AND t.ownerPartnerId = :ownerPartnerId "
            + "AND a.truckId = :truckId")
    Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerIdAndTruckId(
            @Param("companyId") Long companyId,
            @Param("ownerPartnerId") Long ownerPartnerId,
            @Param("truckId") Long truckId,
            Pageable pageable);

    @Query("SELECT a FROM TruckDriverAssignment a, Truck t WHERE a.companyId = t.companyId "
            + "AND a.truckId = t.truckId AND a.companyId = :companyId AND t.ownerPartnerId = :ownerPartnerId "
            + "AND a.driverId = :driverId")
    Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerIdAndDriverId(
            @Param("companyId") Long companyId,
            @Param("ownerPartnerId") Long ownerPartnerId,
            @Param("driverId") Long driverId,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM TruckDriverAssignment a "
            + "WHERE a.companyId = :companyId AND a.truckId = :truckId "
            + "AND a.assignedFrom < :until AND (a.assignedUntil IS NULL OR a.assignedUntil > :from)")
    boolean existsOverlappingForTruck(@Param("companyId") Long companyId,
                                      @Param("truckId") Long truckId,
                                      @Param("from") LocalDateTime from,
                                      @Param("until") LocalDateTime until);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM TruckDriverAssignment a "
            + "WHERE a.companyId = :companyId AND a.driverId = :driverId "
            + "AND a.assignedFrom < :until AND (a.assignedUntil IS NULL OR a.assignedUntil > :from)")
    boolean existsOverlappingForDriver(@Param("companyId") Long companyId,
                                       @Param("driverId") Long driverId,
                                       @Param("from") LocalDateTime from,
                                       @Param("until") LocalDateTime until);
}
