package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveContainerUseCase {
    @Autowired
    private ContainerService containerService;
    @Autowired
    private ChargeService chargeService;


    public ContainerResponse execute(ContainerRequest containerRequest,Long chargeId){
        Container container= containerBuild(containerRequest,chargeId);
        containerService.save(container);
        return containerResponseBuild(container);
    }
    private Container containerBuild(ContainerRequest containerRequest, Long chargeId){
        Charge charge = chargeService.getById(chargeId);
        Container container = new Container();
        container.setCode(containerRequest.getCode());
        container.setDescription(containerRequest.getDescription());
        container.setType(containerRequest.getType());
        container.setWeight(containerRequest.getWeight());
        container.setCharge(charge);
        return container;
    }
    private ContainerResponse containerResponseBuild(Container container){
        return new ContainerResponse(container);
    }

}
