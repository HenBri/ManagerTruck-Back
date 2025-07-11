package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;

import java.util.List;

public interface ChargeService {
    Charge save(Charge charge);
    Charge getById(Long id);
    List<Charge> findAll();
}
