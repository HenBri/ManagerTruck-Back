package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;

import java.util.List;

public interface ContainerService {
    Container save(Container container);
    Container getById(Long id);
    List<Container> findAll();
}
