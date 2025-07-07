package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Journey;

import java.util.List;

public interface JourneyService {
    Journey save(Journey journey);
    Journey getById(Long id);
    List<Journey> findAll();
}
