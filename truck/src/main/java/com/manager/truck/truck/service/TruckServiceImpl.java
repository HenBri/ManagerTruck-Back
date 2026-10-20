package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TruckServiceImpl implements TruckService {

    private final TruckRepository truckRepository;

    @Override
    public Truck save(Truck truck) {
        return truckRepository.save(truck);
    }

    @Override
    public Truck findByCompanyIdAndTruckId(Long companyId, Long truckId) {
        return truckRepository.findByCompanyIdAndTruckId(companyId, truckId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Camión no encontrado con el id " + truckId + " en la empresa " + companyId));
    }

    @Override
    public Truck findByCompanyIdAndTruckIdForUpdate(Long companyId, Long truckId) {
        return truckRepository.findByCompanyIdAndTruckIdForUpdate(companyId, truckId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Camión no encontrado con el id " + truckId + " en la empresa " + companyId));
    }

    @Override
    public boolean existsByCompanyIdAndTruckBoard(Long companyId, String truckBoard) {
        return truckRepository.existsByCompanyIdAndTruckBoard(companyId, truckBoard);
    }

    @Override
    public boolean existsByCompanyIdAndTruckBoardAndTruckIdNot(Long companyId, String truckBoard, Long truckId) {
        return truckRepository.existsByCompanyIdAndTruckBoardAndTruckIdNot(companyId, truckBoard, truckId);
    }

    @Override
    public Page<Truck> findByCompanyId(Long companyId, Pageable pageable) {
        return truckRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<Truck> findByCompanyIdAndOwnerPartnerId(Long companyId, Long ownerPartnerId, Pageable pageable) {
        return truckRepository.findByCompanyIdAndOwnerPartnerId(companyId, ownerPartnerId, pageable);
    }
}
