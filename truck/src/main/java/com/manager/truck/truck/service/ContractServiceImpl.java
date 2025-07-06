package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService{
    @Autowired
    private ContractRepository contractRepository;


    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public Contract findById(Long id) {
        return contractRepository.getById(id);
    }

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }
}
