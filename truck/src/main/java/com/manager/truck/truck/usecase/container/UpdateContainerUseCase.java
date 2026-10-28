package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateContainerUseCase {

    private final ContainerService containerService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContainerResponse execute(Long containerId, ContainerUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Container container = containerService.findById(containerId);
        String code = request.getContainerCode().trim();
        if (!code.equalsIgnoreCase(container.getContainerCode())
                && containerService.existsByContainerCodeAndContainerIdNot(code, containerId)) {
            throw new ConflictException("Ya existe un contenedor con el código " + code);
        }
        container.setContainerCode(code);
        container.setContainerWeight(request.getContainerWeight());
        container.setContainerDescription(request.getContainerDescription());
        container.setContainerType(request.getContainerType());
        if (request.getIsoSize() != null) container.setIsoSize(request.getIsoSize());
        if (request.getContainerFormat() != null) container.setContainerFormat(request.getContainerFormat());
        if (request.getSealNumber() != null) container.setSealNumber(request.getSealNumber());
        if (request.getGrossWeightKg() != null) container.setGrossWeightKg(request.getGrossWeightKg());
        if (request.getTareWeightKg() != null) container.setTareWeightKg(request.getTareWeightKg());
        if (request.getCargoWeightKg() != null) container.setCargoWeightKg(request.getCargoWeightKg());
        if (request.getShippingLine() != null) container.setShippingLine(request.getShippingLine());
        if (request.getCustomsReference() != null) container.setCustomsReference(request.getCustomsReference());
        if (request.getTemperatureC() != null) container.setTemperatureC(request.getTemperatureC());
        Container saved = containerService.update(container);
        return ContainerResponse.from(saved);
    }
}
