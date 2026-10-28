package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.FreightService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveJourneyUseCase {

    private final JourneyService journeyService;
    private final FreightService freightService;
    private final TruckDriverAssignmentService assignmentService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JourneyResponse execute(JourneyRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Freight freight = freightService.findById(request.getFreightId());
        if (freight.getState() == FreightState.CANCELADO) {
            throw new ConflictException("No se pueden crear viajes sobre un flete CANCELADO");
        }
        if (journeyService.findByFreightId(freight.getFreightId()).isPresent()) {
            throw new ConflictException("El flete " + freight.getFreightId() + " ya tiene un viaje asociado");
        }
        if (request.getAssignmentId() != null) {
            TruckDriverAssignment assignment = assignmentService.findById(request.getAssignmentId());
            if (assignment.getAssignedUntil() != null && assignment.getAssignedUntil().isBefore(java.time.OffsetDateTime.now())) {
                throw new ConflictException("La asignación indicada ya finalizó");
            }
        }
        Journey journey = new Journey();
        journey.setFreightId(freight.getFreightId());
        journey.setJourneyOrigin(request.getJourneyOrigin().trim());
        journey.setJourneyDestination(request.getJourneyDestination().trim());
        journey.setJourneyStartDate(request.getJourneyStartDate());
        journey.setJourneyEndDate(request.getJourneyEndDate());
        journey.setJourneyDescription(request.getJourneyDescription());
        journey.setJourneyType(request.getJourneyType());
        journey.setPlannedStartDate(request.getPlannedStartDate());
        journey.setPlannedEndDate(request.getPlannedEndDate());
        journey.setDeliveryConfirmedAt(request.getDeliveryConfirmedAt());
        journey.setRecipientName(request.getRecipientName());
        journey.setProofOfDeliveryReference(request.getProofOfDeliveryReference());
        journey.setObservations(request.getObservations());
        journey.setAssignmentId(request.getAssignmentId());
        journey.setAuxiliaryDriverName(request.getAuxiliaryDriverName());
        journey.setAuxiliaryDriverLicense(request.getAuxiliaryDriverLicense());
        journey.setAuxiliaryReason(request.getAuxiliaryReason());
        journey.setAgreedAmount(request.getAgreedAmount());
        journey.setState(JourneyState.PENDIENTE);

        Journey saved = journeyService.save(journey);
        return JourneyResponse.from(saved);
    }
}
