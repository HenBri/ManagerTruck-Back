package com.manager.truck.truck.usecase.freight;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.FreightUpdateRequest;
import com.manager.truck.truck.domain.dto.response.FreightResponse;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.FreightService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateFreightUseCase {

    private final FreightService freightService;
    private final TruckService truckService;
    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public FreightResponse execute(Long freightId, FreightUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Freight freight = freightService.findById(freightId);

        if (freight.getState() != FreightState.PENDIENTE) {
            throw new ConflictException("Solo se pueden actualizar fletes en estado PENDIENTE; el flete "
                    + freightId + " está en estado " + freight.getState());
        }

        if (request.getTruckId() != null) {
            Truck truck = truckService.findByIdForUpdate(request.getTruckId());
            if (!"ACTIVE".equalsIgnoreCase(truck.getState())) {
                throw new ConflictException("El camión debe estar ACTIVO");
            }
            if (freightService.existsActiveByTruckIdExcluding(truck.getTruckId(), freightId)) {
                throw new ConflictException("El camión ya está asignado a otro flete activo");
            }
            freight.setTruckId(truck.getTruckId());
        }

        if (request.getAuxiliaryProviderId() != null) {
            AuxiliaryProvider provider = auxiliaryProviderService.findById(request.getAuxiliaryProviderId());
            if (!"ACTIVE".equalsIgnoreCase(provider.getState())) {
                throw new ConflictException("El proveedor auxiliar debe estar ACTIVO");
            }
            freight.setAuxiliaryProviderId(provider.getProviderId());
        }

        if (request.getAuxiliaryTruckPlate() != null) {
            freight.setAuxiliaryTruckPlate(request.getAuxiliaryTruckPlate());
        }
        if (request.getFreightDescription() != null) {
            freight.setFreightDescription(request.getFreightDescription());
        }
        if (request.getScheduledDeparture() != null) {
            freight.setScheduledDeparture(request.getScheduledDeparture());
        }
        if (request.getScheduledArrival() != null) {
            freight.setScheduledArrival(request.getScheduledArrival());
        }
        if (request.getActualDeparture() != null) {
            freight.setActualDeparture(request.getActualDeparture());
        }
        if (request.getActualArrival() != null) {
            freight.setActualArrival(request.getActualArrival());
        }
        if (request.getDeliveryNotes() != null) {
            freight.setDeliveryNotes(request.getDeliveryNotes());
        }
        if (request.getFreightAmount() != null) {
            freight.setFreightAmount(request.getFreightAmount());
        }

        Freight saved = freightService.save(freight);
        return FreightResponse.from(saved);
    }
}
