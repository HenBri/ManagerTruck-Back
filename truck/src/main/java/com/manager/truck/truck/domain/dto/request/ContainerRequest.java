package com.manager.truck.truck.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerRequest {
    private String code;
    private String description;
    private String type;
    private Double Weight;

}
