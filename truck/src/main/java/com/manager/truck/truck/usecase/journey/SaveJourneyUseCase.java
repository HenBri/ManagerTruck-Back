package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SaveJourneyUseCase {

    static final Set<JourneyState> ACTIVE_STATES = Set.of(JourneyState.PLANNED, JourneyState.IN_PROGRESS);

    private final JourneyService journeyService;
    private final ContainerService containerService;
    private final ChargeService chargeService;
    private final TruckDriverAssignmentService assignmentService;
    private final TruckService truckService;
    private final DriverService driverService;
    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JourneyResponse execute(Long companyId, JourneyRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear viajes en una empresa inactiva");
        }
        Container container = containerService.findByCompanyIdAndContainerId(companyId, request.getContainerId());
        Charge charge = chargeService.findByCompanyIdAndChargeId(companyId, container.getChargeId());
        if (charge.getChargeState() == ChargeState.CANCELLED) {
            throw new ConflictException("No se pueden crear viajes sobre una carga CANCELLED");
        }
        if (container.getContainerState() == ContainerState.DELIVERED) {
            throw new ConflictException("No se pueden crear viajes sobre un contenedor DELIVERED");
        }
        Journey journey = new Journey();
        journey.setCompanyId(companyId);
        journey.setContainerId(container.getContainerId());
        journey.setJourneyOrigin(request.getJourneyOrigin().trim());
        journey.setJourneyDestination(request.getJourneyDestination().trim());
        journey.setJourneyDescription(trimToNull(request.getJourneyDescription()));
        journey.setJourneyType(trimToNull(request.getJourneyType()));
        journey.setJourneyState(JourneyState.PLANNED);
        journey.setExecutionType(request.getExecutionType());
        if (request.getExecutionType() == JourneyExecutionType.PARTNER) {
            configurePartnerExecution(companyId, request, journey);
        } else {
            configureAuxiliaryExecution(companyId, request, journey);
        }
        Journey saved = journeyService.save(journey);
        return JourneyResponse.from(saved);
    }

    private void configurePartnerExecution(Long companyId, JourneyRequest request, Journey journey) {
        if (request.getAssignmentId() == null) {
            throw new BadRequestException("Un viaje PARTNER requiere assignmentId de una asignación vigente");
        }
        if (request.getAuxiliaryProviderId() != null || request.getAuxiliaryTruckPlate() != null
                || request.getAuxiliaryDriverName() != null || request.getAuxiliaryDriverLicense() != null
                || request.getAuxiliaryReason() != null || request.getAgreedAmount() != null) {
            throw new BadRequestException(
                    "Un viaje PARTNER no admite datos de auxilio; use executionType AUXILIARY para ese escenario");
        }
        TruckDriverAssignment assignment =
                assignmentService.findByCompanyIdAndAssignmentId(companyId, request.getAssignmentId());
        LocalDateTime now = LocalDateTime.now();
        if (assignment.getAssignedFrom().isAfter(now)
                || (assignment.getAssignedUntil() != null && !assignment.getAssignedUntil().isAfter(now))) {
            throw new ConflictException("La asignación " + assignment.getAssignmentId()
                    + " no está vigente para programar el viaje");
        }
        Truck truck = truckService.findByCompanyIdAndTruckId(companyId, assignment.getTruckId());
        if (truck.getTruckState() != TruckStatus.ACTIVE) {
            throw new ConflictException("El camión " + truck.getTruckId() + " está en estado "
                    + truck.getTruckState() + " y no puede ejecutar el viaje");
        }
        Driver driver = driverService.findByCompanyIdAndDriverId(companyId, assignment.getDriverId());
        if (driver.getDriverState() != DriverStatus.ACTIVE) {
            throw new ConflictException("El conductor " + driver.getDriverId() + " está en estado "
                    + driver.getDriverState() + " y no puede ejecutar el viaje");
        }
        if (journeyService.existsActiveByCompanyIdAndAssignmentId(companyId, assignment.getAssignmentId(),
                ACTIVE_STATES)) {
            throw new ConflictException("La asignación " + assignment.getAssignmentId()
                    + " ya tiene un viaje activo (PLANNED o IN_PROGRESS); no se admiten viajes simultáneos");
        }
        journey.setAssignmentId(assignment.getAssignmentId());
    }

    private void configureAuxiliaryExecution(Long companyId, JourneyRequest request, Journey journey) {
        if (request.getAssignmentId() != null) {
            throw new BadRequestException(
                    "Un viaje AUXILIARY no admite assignmentId; use executionType PARTNER para ese escenario");
        }
        if (request.getAuxiliaryProviderId() == null) {
            throw new BadRequestException("Un viaje AUXILIARY requiere auxiliaryProviderId");
        }
        AuxiliaryProvider provider =
                auxiliaryProviderService.findByCompanyIdAndProviderId(companyId, request.getAuxiliaryProviderId());
        if (provider.getProviderStatus() != AuxiliaryProviderStatus.ACTIVE) {
            throw new ConflictException("El proveedor de auxilio " + provider.getProviderId()
                    + " está en estado " + provider.getProviderStatus() + " y no puede ejecutar el viaje");
        }
        String plate = trimRequired(request.getAuxiliaryTruckPlate(), "auxiliaryTruckPlate");
        String driverName = trimRequired(request.getAuxiliaryDriverName(), "auxiliaryDriverName");
        if (request.getAuxiliaryReason() == null) {
            throw new BadRequestException("Un viaje AUXILIARY requiere auxiliaryReason");
        }
        if (request.getAgreedAmount() != null && request.getAgreedAmount().signum() < 0) {
            throw new BadRequestException("agreedAmount no puede ser negativo");
        }
        if (journeyService.existsActiveByCompanyIdAndAuxiliaryTruckPlate(companyId, plate.toLowerCase(),
                ACTIVE_STATES)) {
            throw new ConflictException("Ya existe un viaje activo (PLANNED o IN_PROGRESS) para la placa "
                    + plate + " en esta empresa");
        }
        journey.setAuxiliaryProviderId(provider.getProviderId());
        journey.setAuxiliaryTruckPlate(plate);
        journey.setAuxiliaryDriverName(driverName);
        journey.setAuxiliaryDriverLicense(trimToNull(request.getAuxiliaryDriverLicense()));
        journey.setAuxiliaryReason(request.getAuxiliaryReason());
        journey.setAgreedAmount(request.getAgreedAmount());
    }

    private String trimRequired(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException("Un viaje AUXILIARY requiere " + field + " no vacío");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
