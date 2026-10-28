package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllTrucksUseCase {

    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public TrucksResponse execute(Pageable pageable) {
        Page<Truck> page;
        if (currentUserProvider.isAdmin() || currentUserProvider.isSecretary()) {
            page = truckService.findAll(pageable);
        } else if (currentUserProvider.isPartner()) {
            User user = userService.findById(currentUserProvider.get().userId());
            page = truckService.findByOwnerPartnerId(user.getPersonId(), pageable);
        } else {
            throw new ForbiddenOperationException("No tienes permiso para consultar camiones");
        }
        Page<TruckResponse> content = page.map(TruckResponse::from);
        return new TrucksResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
