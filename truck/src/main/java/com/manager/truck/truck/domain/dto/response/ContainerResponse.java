package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Container;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class ContainerResponse extends CommonResponse {

    private Long containerId;
    private Long chargeId;
    private String containerCode;
    private BigDecimal containerWeight;
    private String containerDescription;
    private String containerType;
    private String isoSize;
    private String containerFormat;
    private String sealNumber;
    private BigDecimal grossWeightKg;
    private BigDecimal tareWeightKg;
    private BigDecimal cargoWeightKg;
    private String shippingLine;
    private String customsReference;
    private BigDecimal temperatureC;
    private OffsetDateTime containerDate;
    private String state;
    private OffsetDateTime dateCreate;

    public ContainerResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ContainerResponse from(Container container) {
        ContainerResponse response = new ContainerResponse();
        response.setContainerId(container.getContainerId());
        response.setChargeId(container.getChargeId());
        response.setContainerCode(container.getContainerCode());
        response.setContainerWeight(container.getContainerWeight());
        response.setContainerDescription(container.getContainerDescription());
        response.setContainerType(container.getContainerType());
        response.setIsoSize(container.getIsoSize());
        response.setContainerFormat(container.getContainerFormat());
        response.setSealNumber(container.getSealNumber());
        response.setGrossWeightKg(container.getGrossWeightKg());
        response.setTareWeightKg(container.getTareWeightKg());
        response.setCargoWeightKg(container.getCargoWeightKg());
        response.setShippingLine(container.getShippingLine());
        response.setCustomsReference(container.getCustomsReference());
        response.setTemperatureC(container.getTemperatureC());
        response.setContainerDate(container.getContainerDate());
        response.setState(container.getState());
        response.setDateCreate(container.getDateCreate());
        return response;
    }

    public String getContainerState() {
        return state;
    }
}
