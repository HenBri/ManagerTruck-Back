package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Truck;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TruckRepository extends JpaRepository<Truck, Long> {

    @Query("SELECT t FROM Truck t WHERE t.truckId = :truckId AND t.isDelete = false")
    Optional<Truck> findActiveById(@Param("truckId") Long truckId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Truck t WHERE t.truckId = :truckId AND t.isDelete = false")
    Optional<Truck> findActiveByIdForUpdate(@Param("truckId") Long truckId);

    @Query("SELECT t FROM Truck t WHERE t.isDelete = false")
    Page<Truck> findAllActive(Pageable pageable);

    @Query("SELECT t FROM Truck t WHERE t.ownerPartnerId = :ownerPartnerId AND t.isDelete = false")
    Page<Truck> findByOwnerPartnerId(@Param("ownerPartnerId") Long ownerPartnerId, Pageable pageable);

    @Query("SELECT t FROM Truck t WHERE t.isDelete = false AND NOT EXISTS " +
            "(SELECT f.freightId FROM Freight f WHERE f.truckId = t.truckId " +
            "AND f.isDelete = false AND f.state IN " +
            "(com.manager.truck.truck.domain.enums.FreightState.PENDIENTE, " +
            "com.manager.truck.truck.domain.enums.FreightState.EN_PROCESO))")
    Page<Truck> findAllAvailableForFreight(Pageable pageable);

    @Query("SELECT t FROM Truck t WHERE t.ownerPartnerId = :ownerPartnerId AND t.isDelete = false " +
            "AND NOT EXISTS (SELECT f.freightId FROM Freight f WHERE f.truckId = t.truckId " +
            "AND f.isDelete = false AND f.state IN " +
            "(com.manager.truck.truck.domain.enums.FreightState.PENDIENTE, " +
            "com.manager.truck.truck.domain.enums.FreightState.EN_PROCESO))")
    Page<Truck> findAvailableForFreightByOwnerPartnerId(@Param("ownerPartnerId") Long ownerPartnerId,
                                                         Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Truck t " +
            "WHERE t.truckBoard = :truckBoard AND t.isDelete = false")
    boolean existsByTruckBoard(@Param("truckBoard") String truckBoard);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Truck t " +
            "WHERE t.truckBoard = :truckBoard AND t.truckId <> :truckId AND t.isDelete = false")
    boolean existsByTruckBoardAndTruckIdNot(@Param("truckBoard") String truckBoard, @Param("truckId") Long truckId);
}
