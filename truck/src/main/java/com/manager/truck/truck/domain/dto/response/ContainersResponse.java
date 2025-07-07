package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Container;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContainersResponse extends CommonResponse{


    private List<Container> containers;

    public ContainersResponse(List<Container> containers) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.containers = containers;
    }
}
