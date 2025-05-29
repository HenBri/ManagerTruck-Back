package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<Truck,Long> {
}
