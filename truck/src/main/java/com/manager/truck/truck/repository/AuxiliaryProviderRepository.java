package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuxiliaryProviderRepository extends JpaRepository<AuxiliaryProvider, Long> {

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.companyId = :companyId AND p.providerId = :providerId")
    Optional<AuxiliaryProvider> findByCompanyIdAndProviderId(@Param("companyId") Long companyId,
                                                             @Param("providerId") Long providerId);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.companyId = :companyId")
    Page<AuxiliaryProvider> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.companyId = :companyId AND p.providerKind = :kind")
    Page<AuxiliaryProvider> findByCompanyIdAndProviderKind(@Param("companyId") Long companyId,
                                                           @Param("kind") AuxiliaryProviderKind kind,
                                                           Pageable pageable);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.companyId = :companyId AND p.providerStatus = :status")
    Page<AuxiliaryProvider> findByCompanyIdAndProviderStatus(@Param("companyId") Long companyId,
                                                             @Param("status") AuxiliaryProviderStatus status,
                                                             Pageable pageable);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.companyId = :companyId "
            + "AND p.providerKind = :kind AND p.providerStatus = :status")
    Page<AuxiliaryProvider> findByCompanyIdAndProviderKindAndProviderStatus(
            @Param("companyId") Long companyId,
            @Param("kind") AuxiliaryProviderKind kind,
            @Param("status") AuxiliaryProviderStatus status,
            Pageable pageable);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.companyId = :companyId "
            + "AND LOWER(p.providerName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<AuxiliaryProvider> findByCompanyIdAndProviderNameContains(@Param("companyId") Long companyId,
                                                                   @Param("name") String name,
                                                                   Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM AuxiliaryProvider p "
            + "WHERE p.companyId = :companyId AND p.providerName = :providerName")
    boolean existsByCompanyIdAndProviderName(@Param("companyId") Long companyId,
                                             @Param("providerName") String providerName);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM AuxiliaryProvider p "
            + "WHERE p.companyId = :companyId AND p.providerName = :providerName "
            + "AND p.providerId <> :providerId")
    boolean existsByCompanyIdAndProviderNameNot(@Param("companyId") Long companyId,
                                                @Param("providerName") String providerName,
                                                @Param("providerId") Long providerId);
}
