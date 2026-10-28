package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChargeService {

    Charge save(Charge charge);

    Charge update(Charge charge);

    Charge findById(Long chargeId);

    Optional<Charge> findOptionalById(Long chargeId);

    Page<Charge> findAll(Pageable pageable);

    Page<Charge> findByContractId(Long contractId, Pageable pageable);
}
