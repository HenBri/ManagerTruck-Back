package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TruckService {

    Truck save(Truck truck);

    Truck findByCompanyIdAndTruckId(Long companyId, Long truckId);

    Truck findByCompanyIdAndTruckIdForUpdate(Long companyId, Long truckId);

    boolean existsByCompanyIdAndTruckBoard(Long companyId, String truckBoard);

    boolean existsByCompanyIdAndTruckBoardAndTruckIdNot(Long companyId, String truckBoard, Long truckId);

    Page<Truck> findByCompanyId(Long companyId, Pageable pageable);

    Page<Truck> findByCompanyIdAndOwnerPartnerId(Long companyId, Long ownerPartnerId, Pageable pageable);
}
