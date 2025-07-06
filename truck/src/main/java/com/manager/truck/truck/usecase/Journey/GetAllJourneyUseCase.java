package com.manager.truck.truck.usecase.Journey;

import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetAllJourneyUseCase {

    @Autowired
    private JourneyService journeyService;

    public JourneysResponse execute(){
        return new JourneysResponse(journeyService.findAll());
    }
}
