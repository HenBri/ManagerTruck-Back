package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.enums.ChargeState;
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
    public Charge findByCompanyIdAndChargeId(Long companyId, Long chargeId) {
        return chargeRepository.findByCompanyIdAndChargeId(companyId, chargeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carga no encontrada con el id " + chargeId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Charge> findOptionalByCompanyIdAndChargeId(Long companyId, Long chargeId) {
        return chargeRepository.findByCompanyIdAndChargeId(companyId, chargeId);
    }

    @Override
    public Page<Charge> findByCompanyId(Long companyId, Pageable pageable) {
        return chargeRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<Charge> findByCompanyIdAndContractId(Long companyId, Long contractId, Pageable pageable) {
        return chargeRepository.findByCompanyIdAndContractId(companyId, contractId, pageable);
    }

    @Override
    public Page<Charge> findByCompanyIdAndChargeState(Long companyId, ChargeState chargeState, Pageable pageable) {
        return chargeRepository.findByCompanyIdAndChargeState(companyId, chargeState, pageable);
    }

    @Override
    public Page<Charge> findByCompanyIdAndContractIdAndChargeState(Long companyId, Long contractId,
                                                                   ChargeState chargeState, Pageable pageable) {
        return chargeRepository.findByCompanyIdAndContractIdAndChargeState(companyId, contractId,
                chargeState, pageable);
    }
}
