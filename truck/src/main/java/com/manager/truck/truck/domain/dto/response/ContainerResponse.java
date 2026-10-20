package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.enums.ContainerState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContainerResponse extends CommonResponse {

    private Long containerId;
    private Long companyId;
    private Long chargeId;
    private String containerCode;
    private BigDecimal containerWeight;
    private String containerDescription;
    private String containerType;
    private ContainerState containerState;
    private LocalDateTime containerDate;

    public ContainerResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ContainerResponse from(Container container) {
        ContainerResponse response = new ContainerResponse();
        response.setContainerId(container.getContainerId());
        response.setCompanyId(container.getCompanyId());
        response.setChargeId(container.getChargeId());
        response.setContainerCode(container.getContainerCode());
        response.setContainerWeight(container.getContainerWeight());
        response.setContainerDescription(container.getContainerDescription());
        response.setContainerType(container.getContainerType());
        response.setContainerState(container.getContainerState());
        response.setContainerDate(container.getContainerDate());
        return response;
    }
}
