package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.ContractState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractStatusRequest {

    @NotNull
    private ContractState state;

    public ContractState getContractState() {
        return state;
    }

    public void setContractState(ContractState state) {
        this.state = state;
    }
}
