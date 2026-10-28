package com.manager.truck.truck.usecase.freight;

import com.manager.truck.truck.domain.Freight;
import com.manager.truck.truck.domain.dto.request.FreightStatusRequest;
import com.manager.truck.truck.domain.dto.response.FreightResponse;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.FreightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateFreightStatusUseCase {

    private static final Map<FreightState, Set<FreightState>> ALLOWED_TRANSITIONS = Map.of(
            FreightState.PENDIENTE, Set.of(FreightState.EN_PROCESO, FreightState.CANCELADO),
            FreightState.EN_PROCESO, Set.of(FreightState.COMPLETADO, FreightState.CANCELADO),
            FreightState.COMPLETADO, Set.of(),
            FreightState.CANCELADO, Set.of()
    );

    private final FreightService freightService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public FreightResponse execute(Long freightId, FreightStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Freight freight = freightService.findById(freightId);

        FreightState current = freight.getState();
        FreightState target = request.getState();

        if (current == target) {
            throw new ConflictException("El flete ya se encuentra en el estado " + current);
        }

        Set<FreightState> validTargets = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!validTargets.contains(target)) {
            throw new ConflictException("Transición de estado inválida para el flete: " + current + " -> " + target);
        }

        freight.setState(target);
        if (target == FreightState.EN_PROCESO && freight.getActualDeparture() == null) {
            freight.setActualDeparture(OffsetDateTime.now());
        } else if (target == FreightState.COMPLETADO && freight.getActualArrival() == null) {
            freight.setActualArrival(OffsetDateTime.now());
        }

        Freight saved = freightService.save(freight);
        return FreightResponse.from(saved);
    }
}

