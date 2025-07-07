package com.manager.truck.truck.usecase.Journey;

import com.manager.truck.truck.domain.*;
import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveJourneyUseCase {
    @Autowired
    private JourneyService journeyService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private UserService userService;



    public JourneyResponse execute(JourneyRequest journeyRequest, Long contractId, Long truckId, Long containerId,Long userId){
        Journey journey = journeyBuild(journeyRequest, contractId,truckId,containerId,userId);
        journeyService.save(journey);
        return journeyResponseBuild(journey);
    }
    private Journey journeyBuild(JourneyRequest journeyRequest, Long contractId,Long truckId, Long containerId,Long userId){

        Container container = containerService.getById(containerId);
        Contract contract = contractService.findById(contractId);
        Truck truck = truckService.getTruckById(truckId);
        User user = userService.findById(userId);

        Journey journey = new Journey();
        journey.setDescription(journeyRequest.getDescription());
        journey.setOrigin(journeyRequest.getOrigin());
        journey.setDestination(journeyRequest.getDestination());
        journey.setType(journeyRequest.getType());
        journey.setContract(contract);
        journey.setTruck(truck);
        journey.setContainer(container);
        journey.setUser(user);
        return journey;
    }
    private JourneyResponse journeyResponseBuild(Journey journey){
        return new JourneyResponse(journey);
    }
}
