package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetTruckByIdUseCase {
    @Autowired
    private TruckService truckService;

    public TruckResponse execute(Long id){
        return new TruckResponse(truckService.getTruckById(id));
    }
}
