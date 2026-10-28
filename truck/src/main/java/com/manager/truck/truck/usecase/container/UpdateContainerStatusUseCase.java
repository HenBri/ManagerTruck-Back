package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateContainerStatusUseCase {

    private final ContainerService containerService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContainerResponse execute(Long containerId, ContainerStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Container container = containerService.findById(containerId);
        String current = container.getState();
        String target = request.getState();
        if (target.equalsIgnoreCase(current)) {
            throw new ConflictException("El contenedor ya se encuentra en el estado " + current);
        }
        container.setState(target);
        Container saved = containerService.update(container);
        return ContainerResponse.from(saved);
    }
}
