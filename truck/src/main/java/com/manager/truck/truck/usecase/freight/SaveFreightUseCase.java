package com.manager.truck.truck.usecase.freight;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.FreightRequest;
import com.manager.truck.truck.domain.dto.response.FreightResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.ContainerService;
import com.manager.truck.truck.service.ContractService;
import com.manager.truck.truck.service.FreightService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveFreightUseCase {

    private final FreightService freightService;
    private final JourneyService journeyService;
    private final ContractService contractService;
    private final ContainerService containerService;
    private final TruckService truckService;
    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public FreightResponse execute(FreightRequest request) {
        currentUserProvider.requireAdminOrSecretary();

        Contract contract = contractService.findById(request.getContractId());
        if (contract.getState() == ContractState.CANCELADO) {
            throw new ConflictException("No se pueden crear fletes sobre un contrato CANCELADO");
        }

        Container container = containerService.findByIdForUpdate(request.getContainerId());
        if ("CANCELADO".equalsIgnoreCase(container.getState())) {
            throw new ConflictException("No se pueden crear fletes sobre un contenedor CANCELADO");
        }
        if (freightService.existsActiveByContainerId(container.getContainerId())) {
            throw new ConflictException("El contenedor ya está asignado a un flete activo");
        }

        String code = request.getFreightCode().trim();
        if (freightService.existsByFreightCode(code)) {
            throw new ConflictException("Ya existe un flete con el código: " + code);
        }

        if (request.getTruckId() != null) {
            Truck truck = truckService.findByIdForUpdate(request.getTruckId());
            if (!"ACTIVE".equalsIgnoreCase(truck.getState())) {
                throw new ConflictException("El camión debe estar ACTIVO");
            }
            if (freightService.existsActiveByTruckId(truck.getTruckId())) {
                throw new ConflictException("El camión ya está asignado a un flete activo");
            }
        }

        if (request.getAuxiliaryProviderId() != null) {
            AuxiliaryProvider provider = auxiliaryProviderService.findById(request.getAuxiliaryProviderId());
            if (!"ACTIVE".equalsIgnoreCase(provider.getState())) {
                throw new ConflictException("El proveedor auxiliar debe estar ACTIVO");
            }
        }

        Freight freight = new Freight();
        freight.setContractId(contract.getContractId());
        freight.setContainerId(container.getContainerId());
        freight.setTruckId(request.getTruckId());
        freight.setAuxiliaryProviderId(request.getAuxiliaryProviderId());
        freight.setAuxiliaryTruckPlate(request.getAuxiliaryTruckPlate());
        freight.setFreightDescription(request.getFreightDescription());
        freight.setFreightCode(code);
        freight.setScheduledDeparture(request.getScheduledDeparture());
        freight.setScheduledArrival(request.getScheduledArrival());
        freight.setFreightAmount(request.getFreightAmount());
        freight.setState(FreightState.PENDIENTE);

        Freight saved = freightService.save(freight);
        Journey journey = new Journey();
        journey.setFreightId(saved.getFreightId());
        journey.setJourneyOrigin(valueOrDefault(contract.getOriginLocation(), "POR DEFINIR"));
        journey.setJourneyDestination(valueOrDefault(contract.getDestinationLocation(), "POR DEFINIR"));
        journey.setJourneyDescription(saved.getFreightDescription());
        journey.setJourneyType(saved.getTruckId() != null ? "CAMION" : "AUXILIAR");
        journey.setPlannedStartDate(saved.getScheduledDeparture());
        journey.setPlannedEndDate(saved.getScheduledArrival());
        journey.setAuxiliaryReason(saved.getAuxiliaryProviderId() != null ? "PROVEEDOR" : null);
        journey.setAgreedAmount(saved.getFreightAmount());
        journey.setState(com.manager.truck.truck.domain.enums.JourneyState.PENDIENTE);
        journeyService.save(journey);
        return FreightResponse.from(saved);
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
