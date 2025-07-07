package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.repository.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChargeServiceImpl implements ChargeService{

    @Autowired
    private ChargeRepository chargeRepository;
    @Override
    public Charge save(Charge charge) {
        return chargeRepository.save(charge);
    }
    @Override
    public Charge getById(Long id) {
        return chargeRepository.getById(id);
    }
    @Override
    public List<Charge> findAll() {
        return chargeRepository.findAll();
    }

}
