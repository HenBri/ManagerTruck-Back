package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.enums.FreightState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FreightService {

    Freight save(Freight freight);

    Freight update(Freight freight);

    Freight findById(Long freightId);

    Optional<Freight> findOptionalById(Long freightId);

    Optional<Freight> findByFreightCode(String freightCode);

    boolean existsByFreightCode(String freightCode);

    boolean existsByFreightCodeAndFreightIdNot(String freightCode, Long freightId);

    Page<Freight> findAll(Pageable pageable);

    Page<Freight> findByContractId(Long contractId, Pageable pageable);

    Page<Freight> findByTruckId(Long truckId, Pageable pageable);

    Page<Freight> findByState(FreightState state, Pageable pageable);
}

