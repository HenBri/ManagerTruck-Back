package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateTruckUseCase {
    @Autowired
    private TruckService truckService;

    public TruckResponse execute(Long id, TruckRequest truckRequest){
        Truck truck = truckBuild(id, truckRequest);
        truckService.save(truck);
        return trucksResponseBuild(truck);
    }
    private Truck truckBuild(Long id, TruckRequest truckRequest){
        Truck truck = truckService.getTruckById(id);
        truck.setPlate(truckRequest.getPlate());
        truck.setModel(truckRequest.getModel());
        truck.setColor(truckRequest.getColor());
        truck.setBrand(truckRequest.getBrand());
        truck.setChassis(truckRequest.getChassis());

        return truck;
    }

    private TruckResponse trucksResponseBuild(Truck truck){
        return new TruckResponse(truck);
    }
}
