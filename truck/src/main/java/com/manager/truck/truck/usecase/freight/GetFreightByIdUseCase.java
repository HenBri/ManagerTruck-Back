package com.manager.truck.truck.usecase.freight;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.FreightResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.FreightService;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFreightByIdUseCase {

    private final FreightService freightService;
    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public FreightResponse execute(Long freightId) {
        Freight freight = freightService.findById(freightId);
        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            if (currentUserProvider.isPartner()) {
                if (freight.getTruckId() == null) {
                    throw new ForbiddenOperationException("No tienes permiso para consultar este flete");
                }
                User user = userService.findById(currentUserProvider.get().userId());
                Truck truck = truckService.findById(freight.getTruckId());
                if (!truck.getOwnerPartnerId().equals(user.getPersonId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar fletes de tus propios camiones");
                }
            } else if (!currentUserProvider.isDriver()) {
                throw new ForbiddenOperationException("No tienes permiso para consultar este flete");
            }
        }
        return FreightResponse.from(freight);
    }
}

