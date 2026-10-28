package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuxiliaryProviderRepository extends JpaRepository<AuxiliaryProvider, Long> {

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.providerId = :providerId AND p.isDelete = false")
    Optional<AuxiliaryProvider> findActiveById(@Param("providerId") Long providerId);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.isDelete = false")
    Page<AuxiliaryProvider> findAllActive(Pageable pageable);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE p.providerKind = :kind AND p.isDelete = false")
    Page<AuxiliaryProvider> findByProviderKind(@Param("kind") AuxiliaryProviderKind kind, Pageable pageable);

    @Query("SELECT p FROM AuxiliaryProvider p WHERE LOWER(p.providerName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isDelete = false")
    Page<AuxiliaryProvider> findByProviderNameContains(@Param("name") String name, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM AuxiliaryProvider p " +
            "WHERE LOWER(p.providerName) = LOWER(:providerName) AND p.isDelete = false")
    boolean existsByProviderName(@Param("providerName") String providerName);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM AuxiliaryProvider p " +
            "WHERE LOWER(p.providerName) = LOWER(:providerName) AND p.providerId <> :providerId AND p.isDelete = false")
    boolean existsByProviderNameAndProviderIdNot(@Param("providerName") String providerName, @Param("providerId") Long providerId);
}
