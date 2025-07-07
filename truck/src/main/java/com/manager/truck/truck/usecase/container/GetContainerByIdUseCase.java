package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetContainerByIdUseCase {
    @Autowired
    private ContainerService containerService;

    public ContainerResponse execute(Long id){
        return new ContainerResponse(containerService.getById(id));
    }
}
