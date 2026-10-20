package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.enums.ContractState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractResponse extends CommonResponse {

    private Long contractId;
    private Long companyId;
    private Long clientId;
    private BigDecimal contractAmount;
    private LocalDate contractDate;
    private String contractDescription;
    private String contractType;
    private ContractState contractState;

    public ContractResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ContractResponse from(Contract contract) {
        ContractResponse response = new ContractResponse();
        response.setContractId(contract.getContractId());
        response.setCompanyId(contract.getCompanyId());
        response.setClientId(contract.getClientId());
        response.setContractAmount(contract.getContractAmount());
        response.setContractDate(contract.getContractDate());
        response.setContractDescription(contract.getContractDescription());
        response.setContractType(contract.getContractType());
        response.setContractState(contract.getContractState());
        return response;
    }
}
