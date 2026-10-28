package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckStatusRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTruckStatusUseCase {

    private final TruckService truckService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckResponse execute(Long truckId, TruckStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Truck truck = truckService.findById(truckId);
        if (request.getState().equalsIgnoreCase(truck.getState())) {
            throw new ConflictException("El camión ya se encuentra en el estado " + request.getState());
        }
        truck.setState(request.getState());
        Truck saved = truckService.save(truck);
        return TruckResponse.from(saved);
    }
}
