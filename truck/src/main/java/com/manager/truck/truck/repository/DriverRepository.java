package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d LEFT JOIN FETCH d.person WHERE d.driverId = :driverId AND d.isDelete = false")
    Optional<Driver> findActiveById(@Param("driverId") Long driverId);

    @Query("SELECT d FROM Driver d LEFT JOIN FETCH d.person WHERE d.isDelete = false")
    Page<Driver> findAllActive(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Driver d " +
            "WHERE d.driverLicense = :license AND d.isDelete = false")
    boolean existsByDriverLicense(@Param("license") String license);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Driver d " +
            "WHERE d.driverLicense = :license AND d.driverId <> :driverId AND d.isDelete = false")
    boolean existsByDriverLicenseAndDriverIdNot(@Param("license") String license, @Param("driverId") Long driverId);
}
