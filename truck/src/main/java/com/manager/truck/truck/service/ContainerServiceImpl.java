package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.repository.ContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContainerServiceImpl implements ContainerService {

    @Autowired
    private ContainerRepository containerRepository;

    public Container save(Container container) {
        return containerRepository.save(container);
    }

    public Container getById(Long id) {
        return containerRepository.getById(id);
    }

    public List<Container> findAll() {
        return containerRepository.findAll();
    }
}
