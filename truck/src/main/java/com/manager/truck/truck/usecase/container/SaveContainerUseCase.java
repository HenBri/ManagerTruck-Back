package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveContainerUseCase {

    private final ContainerService containerService;
    private final ChargeService chargeService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContainerResponse execute(Long companyId, ContainerRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear contenedores en una empresa inactiva");
        }
        Charge charge = chargeService.findByCompanyIdAndChargeId(companyId, request.getChargeId());
        if (charge.getChargeState() == ChargeState.CANCELLED) {
            throw new ConflictException("No se pueden crear contenedores sobre una carga CANCELLED");
        }
        String code = request.getContainerCode().trim();
        if (containerService.existsByCompanyIdAndContainerCode(companyId, code)) {
            throw new ConflictException("Ya existe un contenedor con el código " + code + " en esta empresa");
        }
        Container container = new Container();
        container.setCompanyId(companyId);
        container.setChargeId(charge.getChargeId());
        container.setContainerCode(code);
        container.setContainerWeight(request.getContainerWeight());
        container.setContainerDescription(request.getContainerDescription());
        container.setContainerType(request.getContainerType());
        container.setContainerState(ContainerState.AVAILABLE);
        Container saved = containerService.save(container);
        return ContainerResponse.from(saved);
    }
}
