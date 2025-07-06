package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.dto.response.ContainersResponse;
import com.manager.truck.truck.usecase.container.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContainerController {
    @Autowired
    private SaveContainerUseCase saveContainerUseCase;
    @Autowired
    private UpdateContainerUseCase updateContainerUseCase;
    @Autowired
    private GetAllContainerUseCase getAllContainerUseCase;
    @Autowired
    private GetContainerByIdUseCase getContainerByIdUseCase;
    @Autowired
    private DeleteContainerByUseCase deleteContainerUseCase;


    @PostMapping("saveContainer")
    public ContainerResponse save(@RequestBody ContainerRequest containerRequest){
        return saveContainerUseCase.execute(containerRequest);
    }
    @GetMapping("getAllContainer")
    public ContainersResponse getAll(){
        return getAllContainerUseCase.execute();
    }
    @GetMapping("getContainerById/{id}")
    public ContainerResponse getContainerById(@PathVariable Long id){
        return getContainerByIdUseCase.execute(id);
    }
    @PutMapping("updateContainer/{id}")
    public ContainerResponse update(@PathVariable Long id, @RequestBody ContainerRequest containerRequest){
        return updateContainerUseCase.execute(id, containerRequest);
    }
    @DeleteMapping("deleteContainer/{id}")
    public void delete(@PathVariable Long id){
        deleteContainerUseCase.execute(id);
    }
}
