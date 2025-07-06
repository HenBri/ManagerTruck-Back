package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteContainerByUseCase {
    @Autowired
    private ContainerService containerService;

    public void execute(Long id){
        Container container = containerService.getById(id);
        container.setState(false);
        containerService.save(container);
    }
}
