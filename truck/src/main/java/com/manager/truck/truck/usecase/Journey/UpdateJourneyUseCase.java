package com.manager.truck.truck.usecase.Journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateJourneyUseCase {
    @Autowired
    private JourneyService journeyService;

    public JourneyResponse execute(Long id, JourneyRequest journeyRequest){
        Journey journey = journeyBuild(id, journeyRequest);
        journeyService.save(journey);
        return journeyResponseBuild(journey);
    }
    private Journey journeyBuild(Long id, JourneyRequest journeyRequest){
        Journey journey = journeyService.getById(id);
        journey.setDescription(journeyRequest.getDescription());
        journey.setOrigin(journeyRequest.getOrigin());
        journey.setDestination(journeyRequest.getDestination());
        journey.setType(journeyRequest.getType());
        return journey;
    }
    private JourneyResponse journeyResponseBuild(Journey journey){
        return new JourneyResponse(journey);
    }
}
