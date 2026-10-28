package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.TruckDriverAssignmentTable.NAME)
public class TruckDriverAssignment {

    @Id
    @Column(name = Constants.TruckDriverAssignmentTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @Column(name = Constants.TruckDriverAssignmentTable.TruckId.NAME, nullable = false)
    private Long truckId;

    @Column(name = Constants.TruckDriverAssignmentTable.DriverId.NAME, nullable = false)
    private Long driverId;

    @Column(name = Constants.TruckDriverAssignmentTable.AssignedFrom.NAME, nullable = false)
    private OffsetDateTime assignedFrom;

    @Column(name = Constants.TruckDriverAssignmentTable.AssignedUntil.NAME)
    private OffsetDateTime assignedUntil;

    @Column(name = Constants.TruckDriverAssignmentTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.TruckDriverAssignmentTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.TruckDriverAssignmentTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.TruckDriverAssignmentTable.State.NAME, length = Constants.TruckDriverAssignmentTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}
