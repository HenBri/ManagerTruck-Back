package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JourneyServiceImpl implements JourneyService{
    @Autowired
    private JourneyRepository journeyRepository;

    public Journey save(Journey journey) {
        return journeyRepository.save(journey);
    }

    public Journey getById(Long id) {
        return journeyRepository.getById(id);
    }

    public List<Journey> findAll() {
        return journeyRepository.findAll();
    }
}
