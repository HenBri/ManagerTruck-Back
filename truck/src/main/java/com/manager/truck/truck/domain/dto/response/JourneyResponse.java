package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.AuxiliaryReason;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class JourneyResponse extends CommonResponse {

    private Long journeyId;
    private Long companyId;
    private Long containerId;
    private String journeyOrigin;
    private String journeyDestination;
    private LocalDateTime journeyStartDate;
    private LocalDateTime journeyEndDate;
    private String journeyDescription;
    private String journeyType;
    private JourneyState journeyState;
    private JourneyExecutionType executionType;
    private Long assignmentId;
    private Long auxiliaryProviderId;
    private String auxiliaryTruckPlate;
    private String auxiliaryDriverName;
    private String auxiliaryDriverLicense;
    private AuxiliaryReason auxiliaryReason;
    private BigDecimal agreedAmount;
    private LocalDateTime createdAt;

    public JourneyResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static JourneyResponse from(Journey journey) {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(journey.getJourneyId());
        response.setCompanyId(journey.getCompanyId());
        response.setContainerId(journey.getContainerId());
        response.setJourneyOrigin(journey.getJourneyOrigin());
        response.setJourneyDestination(journey.getJourneyDestination());
        response.setJourneyStartDate(journey.getJourneyStartDate());
        response.setJourneyEndDate(journey.getJourneyEndDate());
        response.setJourneyDescription(journey.getJourneyDescription());
        response.setJourneyType(journey.getJourneyType());
        response.setJourneyState(journey.getJourneyState());
        response.setExecutionType(journey.getExecutionType());
        response.setAssignmentId(journey.getAssignmentId());
        response.setAuxiliaryProviderId(journey.getAuxiliaryProviderId());
        response.setAuxiliaryTruckPlate(journey.getAuxiliaryTruckPlate());
        response.setAuxiliaryDriverName(journey.getAuxiliaryDriverName());
        response.setAuxiliaryDriverLicense(journey.getAuxiliaryDriverLicense());
        response.setAuxiliaryReason(journey.getAuxiliaryReason());
        response.setAgreedAmount(journey.getAgreedAmount());
        response.setCreatedAt(journey.getCreatedAt());
        return response;
    }
}
