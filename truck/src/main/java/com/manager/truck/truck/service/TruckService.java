package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Truck;

import java.util.List;

public interface TruckService {
    Truck save(Truck truck);
    Truck getTruckById(Long id);
    List<Truck> findAll();
}
