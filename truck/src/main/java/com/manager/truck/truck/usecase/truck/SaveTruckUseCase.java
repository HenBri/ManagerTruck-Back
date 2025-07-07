package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveTruckUseCase {
    @Autowired
    private TruckService truckService;
    @Autowired
    private UserService userService;

    public TruckResponse execute(TruckRequest truckRequest,Long userId){
        Truck truck = truckBuild(truckRequest,userId);
        truckService.save(truck);
        return truckResponseBuild(truck);
    }
    private Truck truckBuild(TruckRequest truckRequest, Long userId){
        Truck truck = new Truck();
        User user = userService.findById(userId);
        truck.setPlate(truckRequest.getPlate());
        truck.setModel(truckRequest.getModel());
        truck.setColor(truckRequest.getColor());
        truck.setBrand(truckRequest.getBrand());
        truck.setChassis(truckRequest.getChassis());
        truck.setUser(user);
        return truck;
    }
    private TruckResponse truckResponseBuild(Truck truck){
        return new TruckResponse(truck);
    }

}
