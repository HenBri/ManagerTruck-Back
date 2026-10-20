package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckStatusRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTruckStatusUseCase {

    private final TruckService truckService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckResponse execute(Long companyId, Long truckId, TruckStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Truck truck = truckService.findByCompanyIdAndTruckId(companyId, truckId);
        if (truck.getTruckState() == request.getStatus()) {
            throw new ConflictException("El camión ya se encuentra en el estado " + request.getStatus());
        }
        truck.setTruckState(request.getStatus());
        Truck saved = truckService.save(truck);
        return TruckResponse.from(saved);
    }
}
