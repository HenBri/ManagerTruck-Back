package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.TruckDriverAssignmentTable.NAME)
public class TruckDriverAssignment {

    @Id
    @Column(name = Constants.TruckDriverAssignmentTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @Column(name = Constants.TruckDriverAssignmentTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.TruckDriverAssignmentTable.TruckId.NAME, nullable = false)
    private Long truckId;

    @Column(name = Constants.TruckDriverAssignmentTable.DriverId.NAME, nullable = false)
    private Long driverId;

    @Column(name = Constants.TruckDriverAssignmentTable.AssignedFrom.NAME, nullable = false)
    private LocalDateTime assignedFrom;

    @Column(name = Constants.TruckDriverAssignmentTable.AssignedUntil.NAME)
    private LocalDateTime assignedUntil;
}
