package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.enums.TruckStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TruckResponse extends CommonResponse {

    private Long truckId;
    private Long companyId;
    private Long ownerPartnerId;
    private String truckBoard;
    private String truckBrand;
    private String truckModel;
    private String truckChassis;
    private String truckColor;
    private Integer truckYear;
    private BigDecimal truckCapacity;
    private TruckStatus truckState;
    private LocalDateTime truckCreateDate;

    public TruckResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static TruckResponse from(Truck truck) {
        TruckResponse response = new TruckResponse();
        response.setTruckId(truck.getTruckId());
        response.setCompanyId(truck.getCompanyId());
        response.setOwnerPartnerId(truck.getOwnerPartnerId());
        response.setTruckBoard(truck.getTruckBoard());
        response.setTruckBrand(truck.getTruckBrand());
        response.setTruckModel(truck.getTruckModel());
        response.setTruckChassis(truck.getTruckChassis());
        response.setTruckColor(truck.getTruckColor());
        response.setTruckYear(truck.getTruckYear());
        response.setTruckCapacity(truck.getTruckCapacity());
        response.setTruckState(truck.getTruckState());
        response.setTruckCreateDate(truck.getTruckCreateDate());
        return response;
    }
}
