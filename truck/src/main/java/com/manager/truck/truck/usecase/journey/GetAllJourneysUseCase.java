package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllJourneysUseCase {

    private final JourneyService journeyService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public JourneysResponse execute(Long freightId, Long assignmentId, JourneyState state, Pageable pageable) {
        Page<Journey> page;
        if (currentUserProvider.isAdmin() || currentUserProvider.isSecretary()) {
            if (freightId != null) {
                Optional<Journey> optional = journeyService.findByFreightId(freightId);
                page = optional.<Page<Journey>>map(j -> new PageImpl<>(Collections.singletonList(j), pageable, 1))
                        .orElseGet(() -> Page.empty(pageable));
            } else if (assignmentId != null) {
                page = journeyService.findByAssignmentId(assignmentId, pageable);
            } else if (state != null) {
                page = journeyService.findByState(state, pageable);
            } else {
                page = journeyService.findAll(pageable);
            }
        } else if (currentUserProvider.isDriver() || currentUserProvider.isPartner()) {
            if (assignmentId != null) {
                page = journeyService.findByAssignmentId(assignmentId, pageable);
            } else if (freightId != null) {
                Optional<Journey> optional = journeyService.findByFreightId(freightId);
                page = optional.<Page<Journey>>map(j -> new PageImpl<>(Collections.singletonList(j), pageable, 1))
                        .orElseGet(() -> Page.empty(pageable));
            } else {
                page = journeyService.findAll(pageable);
            }
        } else {
            throw new ForbiddenOperationException("No tienes permiso para consultar viajes");
        }
        Page<JourneyResponse> content = page.map(JourneyResponse::from);
        return new JourneysResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
