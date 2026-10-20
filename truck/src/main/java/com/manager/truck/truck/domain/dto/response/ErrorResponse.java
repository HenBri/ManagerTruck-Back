package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends CommonResponse {

    public ErrorResponse(String code, String message) {
        super(code, message);
    }
}
