package com.manager.truck.truck.usecase.freight;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.FreightResponse;
import com.manager.truck.truck.domain.dto.response.FreightsResponse;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.FreightService;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllFreightsUseCase {

    private final FreightService freightService;
    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public FreightsResponse execute(Long contractId, Long truckId, FreightState state, Pageable pageable) {
        Page<Freight> page;
        if (currentUserProvider.isAdmin() || currentUserProvider.isSecretary()) {
            if (contractId != null) {
                page = freightService.findByContractId(contractId, pageable);
            } else if (truckId != null) {
                page = freightService.findByTruckId(truckId, pageable);
            } else if (state != null) {
                page = freightService.findByState(state, pageable);
            } else {
                page = freightService.findAll(pageable);
            }
        } else if (currentUserProvider.isPartner()) {
            User user = userService.findById(currentUserProvider.get().userId());
            if (truckId != null) {
                Truck truck = truckService.findById(truckId);
                if (!user.getPersonId().equals(truck.getOwnerPartnerId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar fletes de tus propios camiones");
                }
                page = freightService.findByTruckId(truckId, pageable);
            } else {
                throw new ForbiddenOperationException("Debe especificar el truckId de un camión propio");
            }
        } else if (currentUserProvider.isDriver()) {
            if (truckId != null) {
                page = freightService.findByTruckId(truckId, pageable);
            } else {
                page = freightService.findAll(pageable);
            }
        } else {
            throw new ForbiddenOperationException("No tienes permiso para consultar fletes");
        }

        Page<FreightResponse> content = page.map(FreightResponse::from);
        return new FreightsResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}

