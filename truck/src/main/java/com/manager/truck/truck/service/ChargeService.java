package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.enums.ChargeState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChargeService {

    Charge save(Charge charge);

    Charge findByCompanyIdAndChargeId(Long companyId, Long chargeId);

    Optional<Charge> findOptionalByCompanyIdAndChargeId(Long companyId, Long chargeId);

    Page<Charge> findByCompanyId(Long companyId, Pageable pageable);

    Page<Charge> findByCompanyIdAndContractId(Long companyId, Long contractId, Pageable pageable);

    Page<Charge> findByCompanyIdAndChargeState(Long companyId, ChargeState chargeState, Pageable pageable);

    Page<Charge> findByCompanyIdAndContractIdAndChargeState(Long companyId, Long contractId,
                                                            ChargeState chargeState, Pageable pageable);
}
