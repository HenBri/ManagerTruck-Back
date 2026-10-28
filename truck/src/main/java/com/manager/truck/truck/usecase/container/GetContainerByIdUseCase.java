package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetContainerByIdUseCase {

    private final ContainerService containerService;
    private final CurrentUserProvider currentUserProvider;

    public ContainerResponse execute(Long containerId) {
        currentUserProvider.requireAdminOrSecretary();
        Container container = containerService.findById(containerId);
        return ContainerResponse.from(container);
    }
}
