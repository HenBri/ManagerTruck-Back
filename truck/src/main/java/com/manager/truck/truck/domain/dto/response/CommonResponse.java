package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommonResponse extends ResponseConstant {
    private String code;
    private String message;
}
