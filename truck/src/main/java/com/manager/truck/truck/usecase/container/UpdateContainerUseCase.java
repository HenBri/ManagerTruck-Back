package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateContainerUseCase {

    @Autowired
    private ContainerService containerService;

    public ContainerResponse execute(Long id, ContainerRequest containerRequest){
        Container container = containerBuild(id, containerRequest);
        containerService.save(container);
        return containerResponseBuild(container);
    }
    private Container containerBuild(Long id, ContainerRequest containerRequest){
        Container container = containerService.getById(id);
        container.setCode(containerRequest.getCode());
        container.setDescription(containerRequest.getDescription());
        container.setType(containerRequest.getType());
        container.setWeight(containerRequest.getWeight());
        return container;
    }
    private ContainerResponse containerResponseBuild(Container container){
        return new ContainerResponse(container);
    }
}
