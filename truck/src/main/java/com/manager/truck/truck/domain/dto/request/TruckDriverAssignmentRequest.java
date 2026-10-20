package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TruckDriverAssignmentRequest {

    @NotNull
    private Long truckId;

    @NotNull
    private Long driverId;

    @NotNull
    private LocalDateTime assignedFrom;

    private LocalDateTime assignedUntil;
}
