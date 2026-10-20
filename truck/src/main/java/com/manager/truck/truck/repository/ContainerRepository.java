package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.enums.ContainerState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    @Query("SELECT c FROM Container c WHERE c.companyId = :companyId AND c.containerId = :containerId")
    Optional<Container> findByCompanyIdAndContainerId(@Param("companyId") Long companyId,
                                                      @Param("containerId") Long containerId);

    @Query("SELECT c FROM Container c WHERE c.companyId = :companyId")
    Page<Container> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM Container c WHERE c.companyId = :companyId AND c.chargeId = :chargeId")
    Page<Container> findByCompanyIdAndChargeId(@Param("companyId") Long companyId,
                                               @Param("chargeId") Long chargeId,
                                               Pageable pageable);

    @Query("SELECT c FROM Container c WHERE c.companyId = :companyId AND c.containerState = :containerState")
    Page<Container> findByCompanyIdAndContainerState(@Param("companyId") Long companyId,
                                                     @Param("containerState") ContainerState containerState,
                                                     Pageable pageable);

    @Query("SELECT c FROM Container c WHERE c.companyId = :companyId AND c.chargeId = :chargeId "
            + "AND c.containerState = :containerState")
    Page<Container> findByCompanyIdAndChargeIdAndContainerState(
            @Param("companyId") Long companyId,
            @Param("chargeId") Long chargeId,
            @Param("containerState") ContainerState containerState,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Container c "
            + "WHERE c.companyId = :companyId AND c.containerCode = :containerCode")
    boolean existsByCompanyIdAndContainerCode(@Param("companyId") Long companyId,
                                              @Param("containerCode") String containerCode);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Container c "
            + "WHERE c.companyId = :companyId AND c.containerCode = :containerCode "
            + "AND c.containerId <> :containerId")
    boolean existsByCompanyIdAndContainerCodeNot(@Param("companyId") Long companyId,
                                                 @Param("containerCode") String containerCode,
                                                 @Param("containerId") Long containerId);
}
