package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.ChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargeServiceImpl implements ChargeService {

    private final ChargeRepository chargeRepository;

    @Override
    public Charge save(Charge charge) {
        return chargeRepository.save(charge);
    }

    @Override
    public Charge update(Charge charge) {
        return chargeRepository.save(charge);
    }

    @Override
    public Charge findById(Long chargeId) {
        return chargeRepository.findActiveById(chargeId)
                .orElseThrow(() -> new ResourceNotFoundException("Carga no encontrada con id " + chargeId));
    }

    @Override
    public Optional<Charge> findOptionalById(Long chargeId) {
        return chargeRepository.findActiveById(chargeId);
    }

    @Override
    public Page<Charge> findAll(Pageable pageable) {
        return chargeRepository.findAllActive(pageable);
    }

    @Override
    public Page<Charge> findByContractId(Long contractId, Pageable pageable) {
        return chargeRepository.findByContractId(contractId, pageable);
    }
}
