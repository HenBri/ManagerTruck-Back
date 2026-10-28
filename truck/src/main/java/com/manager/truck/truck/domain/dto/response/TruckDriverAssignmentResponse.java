package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class TruckDriverAssignmentResponse extends CommonResponse {

    private Long assignmentId;
    private Long truckId;
    private Long driverId;
    private OffsetDateTime assignedFrom;
    private OffsetDateTime assignedUntil;
    private String state;
    private OffsetDateTime dateCreate;

    public TruckDriverAssignmentResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static TruckDriverAssignmentResponse from(TruckDriverAssignment assignment) {
        TruckDriverAssignmentResponse response = new TruckDriverAssignmentResponse();
        response.setAssignmentId(assignment.getAssignmentId());
        response.setTruckId(assignment.getTruckId());
        response.setDriverId(assignment.getDriverId());
        response.setAssignedFrom(assignment.getAssignedFrom());
        response.setAssignedUntil(assignment.getAssignedUntil());
        response.setState(assignment.getState());
        response.setDateCreate(assignment.getDateCreate());
        return response;
    }
}
