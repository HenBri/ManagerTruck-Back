package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.request.TruckUpdateRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTruckUseCase {

    private final TruckService truckService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckResponse execute(Long companyId, Long truckId, TruckUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Truck truck = truckService.findByCompanyIdAndTruckId(companyId, truckId);
        String board = request.getTruckBoard().trim();
        if (!board.equals(truck.getTruckBoard())
                && truckService.existsByCompanyIdAndTruckBoardAndTruckIdNot(companyId, board, truckId)) {
            throw new ConflictException("Ya existe un camión con la placa " + board + " en esta empresa");
        }
        truck.setTruckBoard(board);
        truck.setTruckBrand(request.getTruckBrand());
        truck.setTruckModel(request.getTruckModel());
        truck.setTruckChassis(request.getTruckChassis());
        truck.setTruckColor(request.getTruckColor());
        truck.setTruckYear(request.getTruckYear());
        truck.setTruckCapacity(request.getTruckCapacity());
        Truck saved = truckService.save(truck);
        return TruckResponse.from(saved);
    }
}
