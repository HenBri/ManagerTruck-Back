package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckUpdateRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTruckUseCase {

    private final TruckService truckService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckResponse execute(Long truckId, TruckUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Truck truck = truckService.findById(truckId);
        String board = request.getTruckBoard().trim();
        if (!board.equals(truck.getTruckBoard())
                && truckService.existsByTruckBoardAndTruckIdNot(board, truckId)) {
            throw new ConflictException("Ya existe un camión con la placa " + board);
        }
        truck.setTruckBoard(board);
        truck.setTruckBrand(request.getTruckBrand());
        truck.setTruckModel(request.getTruckModel());
        truck.setTruckChassis(request.getTruckChassis());
        truck.setTruckColor(request.getTruckColor());
        truck.setTruckYear(request.getTruckYear());
        truck.setTruckCapacity(request.getTruckCapacity());
        truck.setTruckEmptyWeightKg(request.getTruckEmptyWeightKg());
        truck.setTrailerEmptyWeightKg(request.getTrailerEmptyWeightKg());
        truck.setMaxGrossWeightKg(request.getMaxGrossWeightKg());
        truck.setTruckType(request.getTruckType());
        truck.setChassisPlate(request.getChassisPlate());
        truck.setInsuranceExpiration(request.getInsuranceExpiration());
        truck.setTechnicalInspectionExpiration(request.getTechnicalInspectionExpiration());

        Truck saved = truckService.save(truck);
        return TruckResponse.from(saved);
    }
}
