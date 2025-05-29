package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckServiceImpl implements TruckService{

    @Autowired
    private TruckRepository truckRepository;


    @Override
    public Truck save(Truck truck) {
        return truckRepository.save(truck);
    }

    @Override
    public Truck getTruckById(Long id) {
        return truckRepository.getById(id);
    }

    @Override
    public List<Truck> findAll() {
        return truckRepository.findAll();
    }


}
