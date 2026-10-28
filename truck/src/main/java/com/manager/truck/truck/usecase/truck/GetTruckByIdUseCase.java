package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTruckByIdUseCase {

    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public TruckResponse execute(Long truckId) {
        Truck truck = truckService.findById(truckId);
        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            if (!currentUserProvider.isPartner()) {
                throw new ForbiddenOperationException("No tienes permiso para consultar camiones");
            }
            User user = userService.findById(currentUserProvider.get().userId());
            if (user.getPersonId() == null || !user.getPersonId().equals(truck.getOwnerPartnerId())) {
                throw new ForbiddenOperationException("Solo puedes consultar tus propios camiones");
            }
        }
        return TruckResponse.from(truck);
    }
}
