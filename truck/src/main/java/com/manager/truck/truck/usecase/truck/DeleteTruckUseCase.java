package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteTruckUseCase {
    @Autowired
    private TruckService truckService;

    public void execute(Long truckId){
        truckService.save(truckBuild(truckId));
    }

    private Truck truckBuild(Long truckId){
        Truck truck = truckService.getTruckById(truckId);
        truck.setState(false);
        return truck;
    }
}
