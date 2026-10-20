package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TruckDriverAssignmentService {

    TruckDriverAssignment save(TruckDriverAssignment assignment);

    TruckDriverAssignment findByCompanyIdAndAssignmentId(Long companyId, Long assignmentId);

    Truck findTruckForUpdate(Long companyId, Long truckId);

    Driver findDriverForUpdate(Long companyId, Long driverId);

    boolean existsOverlappingForTruck(Long companyId, Long truckId, LocalDateTime from, LocalDateTime until);

    boolean existsOverlappingForDriver(Long companyId, Long driverId, LocalDateTime from, LocalDateTime until);

    Page<TruckDriverAssignment> findByCompanyId(Long companyId, Pageable pageable);

    Page<TruckDriverAssignment> findByCompanyIdAndTruckId(Long companyId, Long truckId, Pageable pageable);

    Page<TruckDriverAssignment> findByCompanyIdAndDriverId(Long companyId, Long driverId, Pageable pageable);

    Page<TruckDriverAssignment> findByCompanyIdAndTruckIdAndDriverId(Long companyId, Long truckId, Long driverId,
                                                                     Pageable pageable);

    Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerId(Long companyId, Long ownerPartnerId,
                                                                 Pageable pageable);

    Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerIdAndTruckId(Long companyId, Long ownerPartnerId,
                                                                           Long truckId, Pageable pageable);

    Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerIdAndDriverId(Long companyId, Long ownerPartnerId,
                                                                            Long driverId, Pageable pageable);

    Optional<Truck> findTruckByCompanyIdAndTruckId(Long companyId, Long truckId);
}
