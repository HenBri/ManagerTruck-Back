package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class JourneyResponse extends CommonResponse {

    private Long journeyId;
    private Long freightId;
    private String journeyOrigin;
    private String journeyDestination;
    private OffsetDateTime journeyStartDate;
    private OffsetDateTime journeyEndDate;
    private String journeyDescription;
    private String journeyType;
    private OffsetDateTime plannedStartDate;
    private OffsetDateTime plannedEndDate;
    private OffsetDateTime deliveryConfirmedAt;
    private String recipientName;
    private String proofOfDeliveryReference;
    private String observations;
    private Long assignmentId;
    private String auxiliaryDriverName;
    private String auxiliaryDriverLicense;
    private String auxiliaryReason;
    private BigDecimal agreedAmount;
    private JourneyState state;
    private OffsetDateTime dateCreate;

    public JourneyResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static JourneyResponse from(Journey journey) {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(journey.getJourneyId());
        response.setFreightId(journey.getFreightId());
        response.setJourneyOrigin(journey.getJourneyOrigin());
        response.setJourneyDestination(journey.getJourneyDestination());
        response.setJourneyStartDate(journey.getJourneyStartDate());
        response.setJourneyEndDate(journey.getJourneyEndDate());
        response.setJourneyDescription(journey.getJourneyDescription());
        response.setJourneyType(journey.getJourneyType());
        response.setPlannedStartDate(journey.getPlannedStartDate());
        response.setPlannedEndDate(journey.getPlannedEndDate());
        response.setDeliveryConfirmedAt(journey.getDeliveryConfirmedAt());
        response.setRecipientName(journey.getRecipientName());
        response.setProofOfDeliveryReference(journey.getProofOfDeliveryReference());
        response.setObservations(journey.getObservations());
        response.setAssignmentId(journey.getAssignmentId());
        response.setAuxiliaryDriverName(journey.getAuxiliaryDriverName());
        response.setAuxiliaryDriverLicense(journey.getAuxiliaryDriverLicense());
        response.setAuxiliaryReason(journey.getAuxiliaryReason());
        response.setAgreedAmount(journey.getAgreedAmount());
        response.setState(journey.getState());
        response.setDateCreate(journey.getDateCreate());
        return response;
    }

    public JourneyState getJourneyState() {
        return state;
    }
}
