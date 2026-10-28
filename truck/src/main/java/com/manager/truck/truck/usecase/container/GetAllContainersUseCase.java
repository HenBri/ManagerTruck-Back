package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.dto.response.ContainersResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllContainersUseCase {

    private final ContainerService containerService;
    private final CurrentUserProvider currentUserProvider;

    public ContainersResponse execute(Long chargeId, Pageable pageable) {
        currentUserProvider.requireAdminOrSecretary();
        Page<Container> page;
        if (chargeId != null) {
            page = containerService.findByChargeId(chargeId, pageable);
        } else {
            page = containerService.findAll(pageable);
        }
        Page<ContainerResponse> content = page.map(ContainerResponse::from);
        return new ContainersResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
