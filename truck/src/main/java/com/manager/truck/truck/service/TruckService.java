package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TruckService {

    Truck save(Truck truck);

    Truck update(Truck truck);

    Truck findById(Long truckId);

    Optional<Truck> findOptionalById(Long truckId);

    Truck findByIdForUpdate(Long truckId);

    boolean existsByTruckBoard(String truckBoard);

    boolean existsByTruckBoardAndTruckIdNot(String truckBoard, Long truckId);

    Page<Truck> findAll(Pageable pageable);

    Page<Truck> findByOwnerPartnerId(Long ownerPartnerId, Pageable pageable);
}
