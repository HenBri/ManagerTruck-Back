package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.response.ContainersResponse;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllContainersUseCase {

    private final ContainerService containerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public ContainersResponse execute(Long companyId, Long chargeId, ContainerState state, Pageable pageable) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Page<Container> page;
        if (chargeId != null && state != null) {
            page = containerService.findByCompanyIdAndChargeIdAndContainerState(companyId, chargeId, state, pageable);
        } else if (chargeId != null) {
            page = containerService.findByCompanyIdAndChargeId(companyId, chargeId, pageable);
        } else if (state != null) {
            page = containerService.findByCompanyIdAndContainerState(companyId, state, pageable);
        } else {
            page = containerService.findByCompanyId(companyId, pageable);
        }
        Page<ContainerResponse> content = page.map(ContainerResponse::from);
        return new ContainersResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
