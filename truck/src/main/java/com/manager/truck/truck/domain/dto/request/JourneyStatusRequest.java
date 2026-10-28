package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.JourneyState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JourneyStatusRequest {

    @NotNull
    private JourneyState status;

    public JourneyState getState() {
        return status;
    }

    public void setState(JourneyState state) {
        this.status = state;
    }
}
