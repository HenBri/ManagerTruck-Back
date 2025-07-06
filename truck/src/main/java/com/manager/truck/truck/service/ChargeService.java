package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;

import java.util.List;

public interface ChargeService {
    Charge save(Charge charge);
    Charge findById(Long id);
    List<Charge> findAll();
}
