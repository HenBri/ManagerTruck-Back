package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JourneyServiceImpl implements JourneyService {

    private final JourneyRepository journeyRepository;

    @Override
    public Journey save(Journey journey) {
        return journeyRepository.save(journey);
    }

    @Override
    public Journey findByCompanyIdAndJourneyId(Long companyId, Long journeyId) {
        return journeyRepository.findByCompanyIdAndJourneyId(companyId, journeyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Viaje no encontrado con el id " + journeyId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Journey> findOptionalByCompanyIdAndJourneyId(Long companyId, Long journeyId) {
        return journeyRepository.findByCompanyIdAndJourneyId(companyId, journeyId);
    }

    @Override
    public Page<Journey> findByCompanyId(Long companyId, Pageable pageable) {
        return journeyRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndContainerId(Long companyId, Long containerId, Pageable pageable) {
        return journeyRepository.findByCompanyIdAndContainerId(companyId, containerId, pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndJourneyState(Long companyId, JourneyState state, Pageable pageable) {
        return journeyRepository.findByCompanyIdAndJourneyState(companyId, state, pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndExecutionType(Long companyId, JourneyExecutionType executionType,
                                                        Pageable pageable) {
        return journeyRepository.findByCompanyIdAndExecutionType(companyId, executionType, pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndContainerIdAndJourneyState(Long companyId, Long containerId,
                                                                      JourneyState state, Pageable pageable) {
        return journeyRepository.findByCompanyIdAndContainerIdAndJourneyState(companyId, containerId, state,
                pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndContainerIdAndExecutionType(Long companyId, Long containerId,
                                                                       JourneyExecutionType executionType,
                                                                       Pageable pageable) {
        return journeyRepository.findByCompanyIdAndContainerIdAndExecutionType(companyId, containerId,
                executionType, pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndJourneyStateAndExecutionType(Long companyId, JourneyState state,
                                                                        JourneyExecutionType executionType,
                                                                        Pageable pageable) {
        return journeyRepository.findByCompanyIdAndJourneyStateAndExecutionType(companyId, state, executionType,
                pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndContainerIdAndJourneyStateAndExecutionType(
            Long companyId, Long containerId, JourneyState state, JourneyExecutionType executionType,
            Pageable pageable) {
        return journeyRepository.findByCompanyIdAndContainerIdAndJourneyStateAndExecutionType(
                companyId, containerId, state, executionType, pageable);
    }

    @Override
    public Page<Journey> findByCompanyIdAndOwnerPartnerId(Long companyId, Long partnerId, Long containerId,
                                                          JourneyState state, JourneyExecutionType executionType,
                                                          Pageable pageable) {
        return journeyRepository.findByCompanyIdAndOwnerPartnerId(companyId, partnerId, containerId, state,
                executionType, pageable);
    }

    @Override
    public Optional<Journey> findOptionalByCompanyIdAndJourneyIdAndOwnerPartnerId(Long companyId, Long journeyId,
                                                                                  Long partnerId) {
        return journeyRepository.findByCompanyIdAndJourneyIdAndOwnerPartnerId(companyId, journeyId, partnerId);
    }

    @Override
    public Page<Journey> findByCompanyIdAndDriverId(Long companyId, Long driverId, Long containerId,
                                                    JourneyState state, JourneyExecutionType executionType,
                                                    Pageable pageable) {
        return journeyRepository.findByCompanyIdAndDriverId(companyId, driverId, containerId, state, executionType,
                pageable);
    }

    @Override
    public Optional<Journey> findOptionalByCompanyIdAndJourneyIdAndDriverId(Long companyId, Long journeyId,
                                                                            Long driverId) {
        return journeyRepository.findByCompanyIdAndJourneyIdAndDriverId(companyId, journeyId, driverId);
    }

    @Override
    public boolean existsActiveByCompanyIdAndAssignmentId(Long companyId, Long assignmentId,
                                                          Set<JourneyState> activeStates) {
        return journeyRepository.existsActiveByCompanyIdAndAssignmentId(companyId, assignmentId, activeStates);
    }

    @Override
    public boolean existsActiveByCompanyIdAndAuxiliaryTruckPlate(Long companyId, String plate,
                                                                 Set<JourneyState> activeStates) {
        return journeyRepository.existsActiveByCompanyIdAndAuxiliaryTruckPlate(companyId, plate, activeStates);
    }
}
