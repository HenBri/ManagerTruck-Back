package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface JourneyService {

    Journey save(Journey journey);

    Journey findByCompanyIdAndJourneyId(Long companyId, Long journeyId);

    Optional<Journey> findOptionalByCompanyIdAndJourneyId(Long companyId, Long journeyId);

    Page<Journey> findByCompanyId(Long companyId, Pageable pageable);

    Page<Journey> findByCompanyIdAndContainerId(Long companyId, Long containerId, Pageable pageable);

    Page<Journey> findByCompanyIdAndJourneyState(Long companyId, JourneyState state, Pageable pageable);

    Page<Journey> findByCompanyIdAndExecutionType(Long companyId, JourneyExecutionType executionType,
                                                  Pageable pageable);

    Page<Journey> findByCompanyIdAndContainerIdAndJourneyState(Long companyId, Long containerId,
                                                               JourneyState state, Pageable pageable);

    Page<Journey> findByCompanyIdAndContainerIdAndExecutionType(Long companyId, Long containerId,
                                                                JourneyExecutionType executionType,
                                                                Pageable pageable);

    Page<Journey> findByCompanyIdAndJourneyStateAndExecutionType(Long companyId, JourneyState state,
                                                                 JourneyExecutionType executionType,
                                                                 Pageable pageable);

    Page<Journey> findByCompanyIdAndContainerIdAndJourneyStateAndExecutionType(
            Long companyId, Long containerId, JourneyState state, JourneyExecutionType executionType,
            Pageable pageable);

    Page<Journey> findByCompanyIdAndOwnerPartnerId(Long companyId, Long partnerId, Long containerId,
                                                   JourneyState state, JourneyExecutionType executionType,
                                                   Pageable pageable);

    Optional<Journey> findOptionalByCompanyIdAndJourneyIdAndOwnerPartnerId(Long companyId, Long journeyId,
                                                                           Long partnerId);

    Page<Journey> findByCompanyIdAndDriverId(Long companyId, Long driverId, Long containerId,
                                             JourneyState state, JourneyExecutionType executionType,
                                             Pageable pageable);

    Optional<Journey> findOptionalByCompanyIdAndJourneyIdAndDriverId(Long companyId, Long journeyId,
                                                                     Long driverId);

    boolean existsActiveByCompanyIdAndAssignmentId(Long companyId, Long assignmentId,
                                                   Set<JourneyState> activeStates);

    boolean existsActiveByCompanyIdAndAuxiliaryTruckPlate(Long companyId, String plate,
                                                          Set<JourneyState> activeStates);
}
