package com.manager.truck.truck.usecase.Journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteJourneyUseCase {
    @Autowired
    private JourneyService journeyService;

    public void execute(Long id){
        journeyService.save(journeyBuild(id));
    }

    private Journey journeyBuild(Long id){
        Journey journey = journeyService.getById(id);
        journey.setState(false);
        return journey;
    }
}
