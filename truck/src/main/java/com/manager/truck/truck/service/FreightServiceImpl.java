package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.FreightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreightServiceImpl implements FreightService {

    private final FreightRepository freightRepository;

    @Override
    public int completeByContractId(Long contractId) {
        return freightRepository.completeByContractId(contractId);
    }

    @Override
    public Freight save(Freight freight) {
        return freightRepository.save(freight);
    }

    @Override
    public Freight update(Freight freight) {
        return freightRepository.save(freight);
    }

    @Override
    public Freight findById(Long freightId) {
        return freightRepository.findActiveById(freightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flete no encontrado con id: " + freightId));
    }

    @Override
    public Optional<Freight> findOptionalById(Long freightId) {
        return freightRepository.findActiveById(freightId);
    }

    @Override
    public Optional<Freight> findByFreightCode(String freightCode) {
        return freightRepository.findByFreightCode(freightCode);
    }

    @Override
    public boolean existsByFreightCode(String freightCode) {
        return freightRepository.existsByFreightCode(freightCode);
    }

    @Override
    public boolean existsByFreightCodeAndFreightIdNot(String freightCode, Long freightId) {
        return freightRepository.existsByFreightCodeAndFreightIdNot(freightCode, freightId);
    }

    @Override
    public boolean existsActiveByTruckId(Long truckId) {
        return freightRepository.existsActiveByTruckId(truckId);
    }

    @Override
    public boolean existsActiveByTruckIdExcluding(Long truckId, Long freightId) {
        return freightRepository.existsActiveByTruckIdExcluding(truckId, freightId);
    }

    @Override
    public boolean existsActiveByContainerId(Long containerId) {
        return freightRepository.existsActiveByContainerId(containerId);
    }

    @Override
    public Page<Freight> findAll(Pageable pageable) {
        return freightRepository.findAllActive(pageable);
    }

    @Override
    public Page<Freight> findByContractId(Long contractId, Pageable pageable) {
        return freightRepository.findByContractId(contractId, pageable);
    }

    @Override
    public Page<Freight> findByTruckId(Long truckId, Pageable pageable) {
        return freightRepository.findByTruckId(truckId, pageable);
    }

    @Override
    public Page<Freight> findByState(FreightState state, Pageable pageable) {
        return freightRepository.findByState(state, pageable);
    }
}
