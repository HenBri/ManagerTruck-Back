package com.manager.truck.truck.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JourneyRequest {
    private String description;
    private String origin;
    private String destination;
    private String type;
}
