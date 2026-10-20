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

    @Query("SELECT t FROM Truck t WHERE t.companyId = :companyId AND t.truckId = :truckId")
    Optional<Truck> findByCompanyIdAndTruckId(@Param("companyId") Long companyId,
                                              @Param("truckId") Long truckId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Truck t WHERE t.companyId = :companyId AND t.truckId = :truckId")
    Optional<Truck> findByCompanyIdAndTruckIdForUpdate(@Param("companyId") Long companyId,
                                                       @Param("truckId") Long truckId);

    @Query("SELECT t FROM Truck t WHERE t.companyId = :companyId")
    Page<Truck> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT t FROM Truck t WHERE t.companyId = :companyId AND t.ownerPartnerId = :ownerPartnerId")
    Page<Truck> findByCompanyIdAndOwnerPartnerId(@Param("companyId") Long companyId,
                                                 @Param("ownerPartnerId") Long ownerPartnerId,
                                                 Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Truck t "
            + "WHERE t.companyId = :companyId AND t.truckBoard = :truckBoard")
    boolean existsByCompanyIdAndTruckBoard(@Param("companyId") Long companyId,
                                           @Param("truckBoard") String truckBoard);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Truck t "
            + "WHERE t.companyId = :companyId AND t.truckBoard = :truckBoard AND t.truckId <> :truckId")
    boolean existsByCompanyIdAndTruckBoardAndTruckIdNot(@Param("companyId") Long companyId,
                                                        @Param("truckBoard") String truckBoard,
                                                        @Param("truckId") Long truckId);
}
