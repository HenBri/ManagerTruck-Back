package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveTruckUseCase {
    @Autowired
    private TruckService truckService;

    public TruckResponse execute(TruckRequest truckRequest){
        Truck truck = truckBuild(truckRequest);
        truckService.save(truck);
        return truckResponseBuild(truck);
    }
    private Truck truckBuild(TruckRequest truckRequest){
        Truck truck = new Truck();
        truck.setPlate(truckRequest.getPlate());
        truck.setModel(truckRequest.getModel());
        truck.setColor(truckRequest.getColor());
        truck.setBrand(truckRequest.getBrand());
        truck.setChassis(truckRequest.getChassis());

        return truck;
    }
    private TruckResponse truckResponseBuild(Truck truck){
        return new TruckResponse(truck);
    }

}
