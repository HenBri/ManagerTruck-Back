package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.enums.FreightState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class FreightResponse extends CommonResponse {

    private Long freightId;
    private Long contractId;
    private Long containerId;
    private Long truckId;
    private Long auxiliaryProviderId;
    private String auxiliaryTruckPlate;
    private String freightDescription;
    private String freightCode;
    private OffsetDateTime scheduledDeparture;
    private OffsetDateTime scheduledArrival;
    private OffsetDateTime actualDeparture;
    private OffsetDateTime actualArrival;
    private String deliveryNotes;
    private BigDecimal freightAmount;
    private FreightState state;
    private OffsetDateTime dateCreate;

    public FreightResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static FreightResponse from(Freight freight) {
        FreightResponse response = new FreightResponse();
        response.setFreightId(freight.getFreightId());
        response.setContractId(freight.getContractId());
        response.setContainerId(freight.getContainerId());
        response.setTruckId(freight.getTruckId());
        response.setAuxiliaryProviderId(freight.getAuxiliaryProviderId());
        response.setAuxiliaryTruckPlate(freight.getAuxiliaryTruckPlate());
        response.setFreightDescription(freight.getFreightDescription());
        response.setFreightCode(freight.getFreightCode());
        response.setScheduledDeparture(freight.getScheduledDeparture());
        response.setScheduledArrival(freight.getScheduledArrival());
        response.setActualDeparture(freight.getActualDeparture());
        response.setActualArrival(freight.getActualArrival());
        response.setDeliveryNotes(freight.getDeliveryNotes());
        response.setFreightAmount(freight.getFreightAmount());
        response.setState(freight.getState());
        response.setDateCreate(freight.getDateCreate());
        return response;
    }
}

