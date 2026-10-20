package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateContainerStatusUseCase {

    private static final Map<ContainerState, Set<ContainerState>> ALLOWED_TRANSITIONS = Map.of(
            ContainerState.AVAILABLE, Set.of(ContainerState.LOADED),
            ContainerState.LOADED, Set.of(ContainerState.IN_TRANSIT),
            ContainerState.IN_TRANSIT, Set.of(ContainerState.DELIVERED),
            ContainerState.DELIVERED, Set.of());

    private final ContainerService containerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContainerResponse execute(Long companyId, Long containerId, ContainerStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Container container = containerService.findByCompanyIdAndContainerId(companyId, containerId);
        ContainerState current = container.getContainerState();
        ContainerState target = request.getStatus();
        if (current == target) {
            throw new ConflictException("El contenedor ya se encuentra en el estado " + current);
        }
        if (!ALLOWED_TRANSITIONS.get(current).contains(target)) {
            throw new ConflictException(
                    "Transición de estado inválida para el contenedor: " + current + " -> " + target);
        }
        container.setContainerState(target);
        Container saved = containerService.save(container);
        return ContainerResponse.from(saved);
    }
}
