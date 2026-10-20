package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TruckDriverAssignmentResponse extends CommonResponse {

    private Long assignmentId;
    private Long companyId;
    private Long truckId;
    private Long driverId;
    private LocalDateTime assignedFrom;
    private LocalDateTime assignedUntil;

    public TruckDriverAssignmentResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static TruckDriverAssignmentResponse from(TruckDriverAssignment assignment) {
        TruckDriverAssignmentResponse response = new TruckDriverAssignmentResponse();
        response.setAssignmentId(assignment.getAssignmentId());
        response.setCompanyId(assignment.getCompanyId());
        response.setTruckId(assignment.getTruckId());
        response.setDriverId(assignment.getDriverId());
        response.setAssignedFrom(assignment.getAssignedFrom());
        response.setAssignedUntil(assignment.getAssignedUntil());
        return response;
    }
}
