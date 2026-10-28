package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Truck;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class TruckResponse extends CommonResponse {

    private Long truckId;
    private Long ownerPartnerId;
    private String truckBoard;
    private String truckBrand;
    private String truckModel;
    private String truckChassis;
    private String truckColor;
    private Integer truckYear;
    private BigDecimal truckCapacity;
    private BigDecimal truckEmptyWeightKg;
    private BigDecimal trailerEmptyWeightKg;
    private BigDecimal maxGrossWeightKg;
    private String truckType;
    private String chassisPlate;
    private LocalDate insuranceExpiration;
    private LocalDate technicalInspectionExpiration;
    private String state;
    private OffsetDateTime dateCreate;

    public TruckResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static TruckResponse from(Truck truck) {
        TruckResponse response = new TruckResponse();
        response.setTruckId(truck.getTruckId());
        response.setOwnerPartnerId(truck.getOwnerPartnerId());
        response.setTruckBoard(truck.getTruckBoard());
        response.setTruckBrand(truck.getTruckBrand());
        response.setTruckModel(truck.getTruckModel());
        response.setTruckChassis(truck.getTruckChassis());
        response.setTruckColor(truck.getTruckColor());
        response.setTruckYear(truck.getTruckYear());
        response.setTruckCapacity(truck.getTruckCapacity());
        response.setTruckEmptyWeightKg(truck.getTruckEmptyWeightKg());
        response.setTrailerEmptyWeightKg(truck.getTrailerEmptyWeightKg());
        response.setMaxGrossWeightKg(truck.getMaxGrossWeightKg());
        response.setTruckType(truck.getTruckType());
        response.setChassisPlate(truck.getChassisPlate());
        response.setInsuranceExpiration(truck.getInsuranceExpiration());
        response.setTechnicalInspectionExpiration(truck.getTechnicalInspectionExpiration());
        response.setState(truck.getState());
        response.setDateCreate(truck.getDateCreate());
        return response;
    }

    public String getTruckState() {
        return state;
    }
}
