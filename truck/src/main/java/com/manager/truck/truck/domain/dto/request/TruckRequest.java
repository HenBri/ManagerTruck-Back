package com.manager.truck.truck.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TruckRequest {
    private String plate;
    private String model;
    private String color;
    private String brand;
    private String chassis;
}
