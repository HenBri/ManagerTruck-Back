package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Driver;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.companyId = :companyId AND d.driverId = :driverId")
    Optional<Driver> findByCompanyIdAndDriverId(@Param("companyId") Long companyId,
                                                @Param("driverId") Long driverId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.companyId = :companyId AND d.driverId = :driverId")
    Optional<Driver> findByCompanyIdAndDriverIdForUpdate(@Param("companyId") Long companyId,
                                                         @Param("driverId") Long driverId);

    @Query("SELECT d FROM Driver d WHERE d.companyId = :companyId")
    Page<Driver> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.companyId = :companyId AND d.userId = :userId")
    Optional<Driver> findByCompanyIdAndUserId(@Param("companyId") Long companyId,
                                              @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Driver d "
            + "WHERE d.companyId = :companyId AND d.userId = :userId")
    boolean existsByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Driver d "
            + "WHERE d.companyId = :companyId AND d.personPartnerId = :personPartnerId")
    boolean existsByCompanyIdAndPersonPartnerId(@Param("companyId") Long companyId,
                                                @Param("personPartnerId") Long personPartnerId);
}
