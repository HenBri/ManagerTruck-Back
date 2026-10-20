package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveTruckUseCase {

    private final TruckService truckService;
    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckResponse execute(Long companyId, TruckRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear camiones en una empresa inactiva");
        }
        if (request.getOwnerPartnerId() == null) {
            throw new BadRequestException("ownerPartnerId es obligatorio: todo camión debe tener un socio propietario");
        }
        Partner owner = partnerService.findByCompanyIdAndPartnerId(companyId, request.getOwnerPartnerId());
        if (owner.getPartnerState() != PartnerStatus.ACTIVE) {
            throw new ConflictException("No se pueden asignar camiones a un socio inactivo");
        }
        if (truckService.existsByCompanyIdAndTruckBoard(companyId, request.getTruckBoard().trim())) {
            throw new ConflictException("Ya existe un camión con la placa " + request.getTruckBoard().trim()
                    + " en esta empresa");
        }
        Truck truck = new Truck();
        truck.setCompanyId(companyId);
        truck.setOwnerPartnerId(owner.getPartnerId());
        truck.setTruckBoard(request.getTruckBoard().trim());
        truck.setTruckBrand(request.getTruckBrand());
        truck.setTruckModel(request.getTruckModel());
        truck.setTruckChassis(request.getTruckChassis());
        truck.setTruckColor(request.getTruckColor());
        truck.setTruckYear(request.getTruckYear());
        truck.setTruckCapacity(request.getTruckCapacity());
        truck.setTruckState(TruckStatus.ACTIVE);
        Truck saved = truckService.save(truck);
        return TruckResponse.from(saved);
    }
}
