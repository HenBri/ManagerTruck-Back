package com.manager.truck.truck.usecase.Journey;

import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetJourneyByIdUseCase {
    @Autowired
    private JourneyService journeyService;

    public JourneyResponse execute(Long id){
        return new JourneyResponse(journeyService.getById(id));
    }
}
