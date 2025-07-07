package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.usecase.Journey.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/journey")
public class JourneyController {
    @Autowired
    private DeleteJourneyUseCase deleteJourneyUseCase;
    @Autowired
    private GetAllJourneyUseCase getAllJourneyUseCase;
    @Autowired
    private GetJourneyByIdUseCase getJourneyByIdUseCase;
    @Autowired
    private SaveJourneyUseCase saveJourneyUseCase;
    @Autowired
    private UpdateJourneyUseCase updateJourneyUseCase;

    @PostMapping("saveJourney/{contractId}/{truckId}/{containerId}/{userId}")
    public JourneyResponse save(@RequestBody JourneyRequest journeyRequest,
                                @PathVariable Long contractId,
                                @PathVariable Long truckId,
                                @PathVariable Long containerId,
                                @PathVariable Long userId){
        return saveJourneyUseCase.execute(journeyRequest, contractId,truckId,containerId,userId);
    }
    @GetMapping("getAllJourney")
    public JourneysResponse getAll(){
        return getAllJourneyUseCase.execute();
    }
    @GetMapping("getJourneyById/{id}")
    public JourneyResponse getJourneyById(@PathVariable Long id){
        return getJourneyByIdUseCase.execute(id);
    }
    @PutMapping("updateJourney/{id}")
    public JourneyResponse update(@PathVariable Long id, @RequestBody JourneyRequest journeyRequest){
        return updateJourneyUseCase.execute(id, journeyRequest);
    }
    @DeleteMapping("deleteJourney/{id}")
    public void delete(@PathVariable Long id){
        deleteJourneyUseCase.execute(id);
    }
}
