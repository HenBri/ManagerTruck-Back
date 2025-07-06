package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.dto.response.ContainersResponse;
import com.manager.truck.truck.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetAllContainerUseCase {
    @Autowired
    private ContainerService containerService;

    public ContainersResponse execute(){
        return new ContainersResponse(containerService.findAll());
    }
}
