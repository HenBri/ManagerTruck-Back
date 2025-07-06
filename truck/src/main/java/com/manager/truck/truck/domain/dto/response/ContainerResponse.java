package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Container;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContainerResponse extends CommonResponse{
    private Container container;

    public ContainerResponse(Container container) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.container = container;
    }
}
