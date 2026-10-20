package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.dto.request.JourneyUpdateRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.JourneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateJourneyUseCase {

    private final JourneyService journeyService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JourneyResponse execute(Long companyId, Long journeyId, JourneyUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Journey journey = journeyService.findByCompanyIdAndJourneyId(companyId, journeyId);
        if (journey.getJourneyState() != JourneyState.PLANNED) {
            throw new ConflictException("Solo se pueden editar los datos de un viaje PLANNED; el viaje "
                    + journeyId + " está en estado " + journey.getJourneyState());
        }
        journey.setJourneyOrigin(request.getJourneyOrigin().trim());
        journey.setJourneyDestination(request.getJourneyDestination().trim());
        journey.setJourneyDescription(trimToNull(request.getJourneyDescription()));
        journey.setJourneyType(trimToNull(request.getJourneyType()));
        Journey saved = journeyService.save(journey);
        return JourneyResponse.from(saved);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
