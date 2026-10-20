package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.DriverRepository;
import com.manager.truck.truck.repository.TruckDriverAssignmentRepository;
import com.manager.truck.truck.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TruckDriverAssignmentServiceImpl implements TruckDriverAssignmentService {

    private final TruckDriverAssignmentRepository assignmentRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;

    @Override
    public TruckDriverAssignment save(TruckDriverAssignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    public TruckDriverAssignment findByCompanyIdAndAssignmentId(Long companyId, Long assignmentId) {
        return assignmentRepository.findByCompanyIdAndAssignmentId(companyId, assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación no encontrada con el id " + assignmentId + " en la empresa " + companyId));
    }

    @Override
    public Truck findTruckForUpdate(Long companyId, Long truckId) {
        return truckRepository.findByCompanyIdAndTruckIdForUpdate(companyId, truckId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Camión no encontrado con el id " + truckId + " en la empresa " + companyId));
    }

    @Override
    public Driver findDriverForUpdate(Long companyId, Long driverId) {
        return driverRepository.findByCompanyIdAndDriverIdForUpdate(companyId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor no encontrado con el id " + driverId + " en la empresa " + companyId));
    }

    @Override
    public boolean existsOverlappingForTruck(Long companyId, Long truckId, LocalDateTime from, LocalDateTime until) {
        return assignmentRepository.existsOverlappingForTruck(companyId, truckId, from, until);
    }

    @Override
    public boolean existsOverlappingForDriver(Long companyId, Long driverId, LocalDateTime from, LocalDateTime until) {
        return assignmentRepository.existsOverlappingForDriver(companyId, driverId, from, until);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyId(Long companyId, Pageable pageable) {
        return assignmentRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyIdAndTruckId(Long companyId, Long truckId, Pageable pageable) {
        return assignmentRepository.findByCompanyIdAndTruckId(companyId, truckId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyIdAndDriverId(Long companyId, Long driverId, Pageable pageable) {
        return assignmentRepository.findByCompanyIdAndDriverId(companyId, driverId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyIdAndTruckIdAndDriverId(Long companyId, Long truckId,
                                                                            Long driverId, Pageable pageable) {
        return assignmentRepository.findByCompanyIdAndTruckIdAndDriverId(companyId, truckId, driverId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerId(Long companyId, Long ownerPartnerId,
                                                                        Pageable pageable) {
        return assignmentRepository.findByCompanyIdAndOwnerPartnerId(companyId, ownerPartnerId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerIdAndTruckId(Long companyId, Long ownerPartnerId,
                                                                                  Long truckId, Pageable pageable) {
        return assignmentRepository.findByCompanyIdAndOwnerPartnerIdAndTruckId(
                companyId, ownerPartnerId, truckId, pageable);
    }

    @Override
    public Page<TruckDriverAssignment> findByCompanyIdAndOwnerPartnerIdAndDriverId(Long companyId, Long ownerPartnerId,
                                                                                   Long driverId, Pageable pageable) {
        return assignmentRepository.findByCompanyIdAndOwnerPartnerIdAndDriverId(
                companyId, ownerPartnerId, driverId, pageable);
    }

    @Override
    public Optional<Truck> findTruckByCompanyIdAndTruckId(Long companyId, Long truckId) {
        return truckRepository.findByCompanyIdAndTruckId(companyId, truckId);
    }
}
