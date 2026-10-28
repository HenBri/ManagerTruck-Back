package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TruckServiceImpl implements TruckService {

    private final TruckRepository truckRepository;

    @Override
    public Truck save(Truck truck) {
        return truckRepository.save(truck);
    }

    @Override
    public Truck update(Truck truck) {
        return truckRepository.save(truck);
    }

    @Override
    public Truck findById(Long truckId) {
        return truckRepository.findActiveById(truckId)
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con el id " + truckId));
    }

    @Override
    public Optional<Truck> findOptionalById(Long truckId) {
        return truckRepository.findActiveById(truckId);
    }

    @Override
    public Truck findByIdForUpdate(Long truckId) {
        return truckRepository.findActiveByIdForUpdate(truckId)
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con el id " + truckId));
    }

    @Override
    public boolean existsByTruckBoard(String truckBoard) {
        return truckRepository.existsByTruckBoard(truckBoard);
    }

    @Override
    public boolean existsByTruckBoardAndTruckIdNot(String truckBoard, Long truckId) {
        return truckRepository.existsByTruckBoardAndTruckIdNot(truckBoard, truckId);
    }

    @Override
    public Page<Truck> findAll(Pageable pageable) {
        return truckRepository.findAllActive(pageable);
    }

    @Override
    public Page<Truck> findByOwnerPartnerId(Long ownerPartnerId, Pageable pageable) {
        return truckRepository.findByOwnerPartnerId(ownerPartnerId, pageable);
    }
}
