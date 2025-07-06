package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Contract;

import java.util.List;

public interface ContractService {
    Contract save(Contract contract);
    Contract findById(Long id);
    List<Contract> findAll();
}
