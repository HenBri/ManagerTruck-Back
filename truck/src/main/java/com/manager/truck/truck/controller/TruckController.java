package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.usecase.truck.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
public class TruckController {
    @Autowired
    private SaveTruckUseCase saveTruck;
    @Autowired
    private GetTruckByIdUseCase getTruckByIdUseCase;
    @Autowired
    private GetAllTruckUseCase getAllTruckUseCase;
    @Autowired
    private DeleteTruckUseCase deleteTruckUseCase;
    @Autowired
    private UpdateTruckUseCase updateTruck;

    @PostMapping("/saveTruck")
    public TruckResponse save(@RequestBody TruckRequest truckRequest){
        return saveTruck.execute(truckRequest);
    }
    @GetMapping("getById/{id}")
    public TruckResponse getById(@PathVariable Long id){
        return getTruckByIdUseCase.execute(id);
    }
    @GetMapping("getAllTruck")
    public TrucksResponse getAll(){
        return getAllTruckUseCase.execute();
    }
    @DeleteMapping("deleteTruckbyId/{id}")
    public void deleteById(@PathVariable Long id){
        deleteTruckUseCase.execute(id);
    }
    @PutMapping("updatetruck/{id}")
    public TruckResponse update(@PathVariable Long id, @RequestBody TruckRequest truckRequest){
        return updateTruck.execute(id,truckRequest);
    }

}
