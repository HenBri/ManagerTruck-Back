package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveContainerUseCase {

    private final ContainerService containerService;
    private final ChargeService chargeService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContainerResponse execute(ContainerRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Charge charge = chargeService.findById(request.getChargeId());
        String code = request.getContainerCode().trim();
        if (containerService.existsByContainerCode(code)) {
            throw new ConflictException("Ya existe un contenedor con el código " + code);
        }
        Container container = new Container();
        container.setChargeId(charge.getChargeId());
        container.setContainerCode(code);
        container.setContainerWeight(request.getContainerWeight());
        container.setContainerDescription(request.getContainerDescription());
        container.setContainerType(request.getContainerType());
        container.setIsoSize(request.getIsoSize());
        container.setContainerFormat(request.getContainerFormat());
        container.setSealNumber(request.getSealNumber());
        container.setGrossWeightKg(request.getGrossWeightKg());
        container.setTareWeightKg(request.getTareWeightKg());
        container.setCargoWeightKg(request.getCargoWeightKg());
        container.setShippingLine(request.getShippingLine());
        container.setCustomsReference(request.getCustomsReference());
        container.setTemperatureC(request.getTemperatureC());
        container.setState("AVAILABLE");
        Container saved = containerService.save(container);
        return ContainerResponse.from(saved);
    }
}
