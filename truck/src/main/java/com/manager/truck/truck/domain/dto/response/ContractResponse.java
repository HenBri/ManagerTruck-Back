package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.OperationType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class ContractResponse extends CommonResponse {

    private Long contractId;
    private Long clientId;
    private String contractCode;
    private OperationType operationType;
    private String originLocation;
    private String destinationLocation;
    private LocalDate expectedStartDate;
    private LocalDate expectedDeliveryDate;
    private OffsetDateTime actualDeliveryDate;
    private String referenceDocument;
    private BigDecimal declaredTotalWeightKg;
    private Integer expectedContainerCount;
    private String observations;
    private BigDecimal contractAmount;
    private LocalDate contractDate;
    private String contractDescription;
    private String contractType;
    private ContractState state;
    private OffsetDateTime dateCreate;

    public ContractResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ContractResponse from(Contract contract) {
        ContractResponse response = new ContractResponse();
        response.setContractId(contract.getContractId());
        response.setClientId(contract.getClientId());
        response.setContractCode(contract.getContractCode());
        response.setOperationType(contract.getOperationType());
        response.setOriginLocation(contract.getOriginLocation());
        response.setDestinationLocation(contract.getDestinationLocation());
        response.setExpectedStartDate(contract.getExpectedStartDate());
        response.setExpectedDeliveryDate(contract.getExpectedDeliveryDate());
        response.setActualDeliveryDate(contract.getActualDeliveryDate());
        response.setReferenceDocument(contract.getReferenceDocument());
        response.setDeclaredTotalWeightKg(contract.getDeclaredTotalWeightKg());
        response.setExpectedContainerCount(contract.getExpectedContainerCount());
        response.setObservations(contract.getObservations());
        response.setContractAmount(contract.getContractAmount());
        response.setContractDate(contract.getContractDate());
        response.setContractDescription(contract.getContractDescription());
        response.setContractType(contract.getContractType());
        response.setState(contract.getState());
        response.setDateCreate(contract.getDateCreate());
        return response;
    }

    public ContractState getContractState() {
        return state;
    }
}
