package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.dto.request.JourneyUpdateRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.JourneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateJourneyUseCase {

    private final JourneyService journeyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JourneyResponse execute(Long journeyId, JourneyUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Journey journey = journeyService.findById(journeyId);
        if (journey.getState() != JourneyState.PENDIENTE) {
            throw new ConflictException("Solo se pueden editar los datos de un viaje PENDIENTE; el viaje "
                    + journeyId + " está en estado " + journey.getState());
        }
        if (request.getJourneyOrigin() != null) {
            journey.setJourneyOrigin(request.getJourneyOrigin().trim());
        }
        if (request.getJourneyDestination() != null) {
            journey.setJourneyDestination(request.getJourneyDestination().trim());
        }
        if (request.getJourneyStartDate() != null) {
            journey.setJourneyStartDate(request.getJourneyStartDate());
        }
        if (request.getJourneyEndDate() != null) {
            journey.setJourneyEndDate(request.getJourneyEndDate());
        }
        if (request.getJourneyDescription() != null) {
            journey.setJourneyDescription(request.getJourneyDescription());
        }
        if (request.getJourneyType() != null) {
            journey.setJourneyType(request.getJourneyType());
        }
        if (request.getPlannedStartDate() != null) {
            journey.setPlannedStartDate(request.getPlannedStartDate());
        }
        if (request.getPlannedEndDate() != null) {
            journey.setPlannedEndDate(request.getPlannedEndDate());
        }
        if (request.getDeliveryConfirmedAt() != null) {
            journey.setDeliveryConfirmedAt(request.getDeliveryConfirmedAt());
        }
        if (request.getRecipientName() != null) {
            journey.setRecipientName(request.getRecipientName());
        }
        if (request.getProofOfDeliveryReference() != null) {
            journey.setProofOfDeliveryReference(request.getProofOfDeliveryReference());
        }
        if (request.getObservations() != null) {
            journey.setObservations(request.getObservations());
        }
        if (request.getAssignmentId() != null) {
            journey.setAssignmentId(request.getAssignmentId());
        }
        if (request.getAuxiliaryDriverName() != null) {
            journey.setAuxiliaryDriverName(request.getAuxiliaryDriverName());
        }
        if (request.getAuxiliaryDriverLicense() != null) {
            journey.setAuxiliaryDriverLicense(request.getAuxiliaryDriverLicense());
        }
        if (request.getAuxiliaryReason() != null) {
            journey.setAuxiliaryReason(request.getAuxiliaryReason());
        }
        if (request.getAgreedAmount() != null) {
            journey.setAgreedAmount(request.getAgreedAmount());
        }

        Journey saved = journeyService.save(journey);
        return JourneyResponse.from(saved);
    }
}
