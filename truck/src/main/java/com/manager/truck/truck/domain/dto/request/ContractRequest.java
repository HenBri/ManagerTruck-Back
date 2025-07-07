package com.manager.truck.truck.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContractRequest {
    private String description;
    private String type;
    private Double amount;
}
