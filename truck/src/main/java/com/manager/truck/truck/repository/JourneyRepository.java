package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface JourneyRepository extends JpaRepository<Journey, Long> {

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.journeyId = :journeyId")
    Optional<Journey> findByCompanyIdAndJourneyId(@Param("companyId") Long companyId,
                                                  @Param("journeyId") Long journeyId);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId")
    Page<Journey> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.containerId = :containerId")
    Page<Journey> findByCompanyIdAndContainerId(@Param("companyId") Long companyId,
                                                @Param("containerId") Long containerId,
                                                Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.journeyState = :state")
    Page<Journey> findByCompanyIdAndJourneyState(@Param("companyId") Long companyId,
                                                 @Param("state") JourneyState state,
                                                 Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.executionType = :executionType")
    Page<Journey> findByCompanyIdAndExecutionType(@Param("companyId") Long companyId,
                                                  @Param("executionType") JourneyExecutionType executionType,
                                                  Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.containerId = :containerId "
            + "AND j.journeyState = :state")
    Page<Journey> findByCompanyIdAndContainerIdAndJourneyState(
            @Param("companyId") Long companyId,
            @Param("containerId") Long containerId,
            @Param("state") JourneyState state,
            Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.containerId = :containerId "
            + "AND j.executionType = :executionType")
    Page<Journey> findByCompanyIdAndContainerIdAndExecutionType(
            @Param("companyId") Long companyId,
            @Param("containerId") Long containerId,
            @Param("executionType") JourneyExecutionType executionType,
            Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.journeyState = :state "
            + "AND j.executionType = :executionType")
    Page<Journey> findByCompanyIdAndJourneyStateAndExecutionType(
            @Param("companyId") Long companyId,
            @Param("state") JourneyState state,
            @Param("executionType") JourneyExecutionType executionType,
            Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.containerId = :containerId "
            + "AND j.journeyState = :state AND j.executionType = :executionType")
    Page<Journey> findByCompanyIdAndContainerIdAndJourneyStateAndExecutionType(
            @Param("companyId") Long companyId,
            @Param("containerId") Long containerId,
            @Param("state") JourneyState state,
            @Param("executionType") JourneyExecutionType executionType,
            Pageable pageable);

    @Query("SELECT j FROM Journey j, TruckDriverAssignment tda, Truck t "
            + "WHERE j.companyId = :companyId AND j.assignmentId = tda.assignmentId "
            + "AND tda.companyId = :companyId AND tda.truckId = t.truckId "
            + "AND t.companyId = :companyId AND t.ownerPartnerId = :partnerId "
            + "AND (:containerId IS NULL OR j.containerId = :containerId) "
            + "AND (:state IS NULL OR j.journeyState = :state) "
            + "AND (:executionType IS NULL OR j.executionType = :executionType)")
    Page<Journey> findByCompanyIdAndOwnerPartnerId(@Param("companyId") Long companyId,
                                                   @Param("partnerId") Long partnerId,
                                                   @Param("containerId") Long containerId,
                                                   @Param("state") JourneyState state,
                                                   @Param("executionType") JourneyExecutionType executionType,
                                                   Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.journeyId = :journeyId "
            + "AND j.assignmentId IN (SELECT tda.assignmentId FROM TruckDriverAssignment tda "
            + "WHERE tda.companyId = :companyId AND tda.truckId IN "
            + "(SELECT t.truckId FROM Truck t WHERE t.companyId = :companyId AND t.ownerPartnerId = :partnerId))")
    Optional<Journey> findByCompanyIdAndJourneyIdAndOwnerPartnerId(@Param("companyId") Long companyId,
                                                                   @Param("journeyId") Long journeyId,
                                                                   @Param("partnerId") Long partnerId);

    @Query("SELECT j FROM Journey j, TruckDriverAssignment tda "
            + "WHERE j.companyId = :companyId AND j.assignmentId = tda.assignmentId "
            + "AND tda.companyId = :companyId AND tda.driverId = :driverId "
            + "AND (:containerId IS NULL OR j.containerId = :containerId) "
            + "AND (:state IS NULL OR j.journeyState = :state) "
            + "AND (:executionType IS NULL OR j.executionType = :executionType)")
    Page<Journey> findByCompanyIdAndDriverId(@Param("companyId") Long companyId,
                                             @Param("driverId") Long driverId,
                                             @Param("containerId") Long containerId,
                                             @Param("state") JourneyState state,
                                             @Param("executionType") JourneyExecutionType executionType,
                                             Pageable pageable);

    @Query("SELECT j FROM Journey j WHERE j.companyId = :companyId AND j.journeyId = :journeyId "
            + "AND j.assignmentId IN (SELECT tda.assignmentId FROM TruckDriverAssignment tda "
            + "WHERE tda.companyId = :companyId AND tda.driverId = :driverId)")
    Optional<Journey> findByCompanyIdAndJourneyIdAndDriverId(@Param("companyId") Long companyId,
                                                             @Param("journeyId") Long journeyId,
                                                             @Param("driverId") Long driverId);

    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM Journey j "
            + "WHERE j.companyId = :companyId AND j.assignmentId = :assignmentId "
            + "AND j.journeyState IN :activeStates")
    boolean existsActiveByCompanyIdAndAssignmentId(@Param("companyId") Long companyId,
                                                   @Param("assignmentId") Long assignmentId,
                                                   @Param("activeStates") Set<JourneyState> activeStates);

    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM Journey j "
            + "WHERE j.companyId = :companyId AND LOWER(TRIM(j.auxiliaryTruckPlate)) = :plate "
            + "AND j.journeyState IN :activeStates")
    boolean existsActiveByCompanyIdAndAuxiliaryTruckPlate(@Param("companyId") Long companyId,
                                                          @Param("plate") String plate,
                                                          @Param("activeStates") Set<JourneyState> activeStates);
}
