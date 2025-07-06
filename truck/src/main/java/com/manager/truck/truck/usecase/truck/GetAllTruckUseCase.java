package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllTruckUseCase {
    @Autowired
    private TruckService truckService;

    public TrucksResponse execute(){
        List<Truck> trucks = truckService.findAll();
        return new TrucksResponse(trucks);
    }
}
