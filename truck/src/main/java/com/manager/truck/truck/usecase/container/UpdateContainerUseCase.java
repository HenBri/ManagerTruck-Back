package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateContainerUseCase {

    private final ContainerService containerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContainerResponse execute(Long companyId, Long containerId, ContainerUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Container container = containerService.findByCompanyIdAndContainerId(companyId, containerId);
        String code = request.getContainerCode().trim();
        if (!code.equals(container.getContainerCode())
                && containerService.existsByCompanyIdAndContainerCodeNot(companyId, code, containerId)) {
            throw new ConflictException("Ya existe un contenedor con el código " + code + " en esta empresa");
        }
        container.setContainerCode(code);
        container.setContainerWeight(request.getContainerWeight());
        container.setContainerDescription(request.getContainerDescription());
        container.setContainerType(request.getContainerType());
        Container saved = containerService.save(container);
        return ContainerResponse.from(saved);
    }
}
