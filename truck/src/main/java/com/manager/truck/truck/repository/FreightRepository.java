package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.enums.FreightState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FreightRepository extends JpaRepository<Freight, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Freight f SET f.state = com.manager.truck.truck.domain.enums.FreightState.COMPLETADO, " +
            "f.actualArrival = COALESCE(f.actualArrival, CURRENT_TIMESTAMP) " +
            "WHERE f.contractId = :contractId AND f.isDelete = false")
    int completeByContractId(@Param("contractId") Long contractId);

    @Query("SELECT f FROM Freight f WHERE f.freightId = :freightId AND f.isDelete = false")
    Optional<Freight> findActiveById(@Param("freightId") Long freightId);

    @Query("SELECT f FROM Freight f WHERE f.freightCode = :code AND f.isDelete = false")
    Optional<Freight> findByFreightCode(@Param("code") String code);

    @Query("SELECT f FROM Freight f WHERE f.contractId = :contractId AND f.isDelete = false")
    Page<Freight> findByContractId(@Param("contractId") Long contractId, Pageable pageable);

    @Query("SELECT f FROM Freight f WHERE f.truckId = :truckId AND f.isDelete = false")
    Page<Freight> findByTruckId(@Param("truckId") Long truckId, Pageable pageable);

    @Query("SELECT f FROM Freight f WHERE f.state = :state AND f.isDelete = false")
    Page<Freight> findByState(@Param("state") FreightState state, Pageable pageable);

    @Query("SELECT f FROM Freight f WHERE f.isDelete = false")
    Page<Freight> findAllActive(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Freight f " +
            "WHERE f.freightCode = :freightCode AND f.isDelete = false")
    boolean existsByFreightCode(@Param("freightCode") String freightCode);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Freight f " +
            "WHERE f.freightCode = :freightCode AND f.freightId <> :freightId AND f.isDelete = false")
    boolean existsByFreightCodeAndFreightIdNot(@Param("freightCode") String freightCode, @Param("freightId") Long freightId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Freight f " +
            "WHERE f.truckId = :truckId AND f.isDelete = false " +
            "AND f.state IN (com.manager.truck.truck.domain.enums.FreightState.PENDIENTE, " +
            "com.manager.truck.truck.domain.enums.FreightState.EN_PROCESO)")
    boolean existsActiveByTruckId(@Param("truckId") Long truckId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Freight f " +
            "WHERE f.truckId = :truckId AND f.freightId <> :freightId AND f.isDelete = false " +
            "AND f.state IN (com.manager.truck.truck.domain.enums.FreightState.PENDIENTE, " +
            "com.manager.truck.truck.domain.enums.FreightState.EN_PROCESO)")
    boolean existsActiveByTruckIdExcluding(@Param("truckId") Long truckId, @Param("freightId") Long freightId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Freight f " +
            "WHERE f.containerId = :containerId AND f.isDelete = false " +
            "AND f.state IN (com.manager.truck.truck.domain.enums.FreightState.PENDIENTE, " +
            "com.manager.truck.truck.domain.enums.FreightState.EN_PROCESO)")
    boolean existsActiveByContainerId(@Param("containerId") Long containerId);
}
