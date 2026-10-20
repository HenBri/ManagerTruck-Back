package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JourneyUpdateRequest {

    @NotBlank
    @Size(max = 255)
    private String journeyOrigin;

    @NotBlank
    @Size(max = 255)
    private String journeyDestination;

    @Size(max = 255)
    private String journeyDescription;

    @Size(max = 50)
    private String journeyType;
}
