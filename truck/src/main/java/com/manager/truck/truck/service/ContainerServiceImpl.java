package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContainerServiceImpl implements ContainerService {

    @Autowired
    private ContainerService containerService;

    public Container save(Container container) {
        return containerService.save(container);
    }

    public Container getById(Long id) {
        return containerService.getById(id);
    }

    public List<Container> findAll() {
        return containerService.findAll();
    }
}
