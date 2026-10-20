package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId AND c.clientId = :clientId")
    Optional<Client> findByCompanyIdAndClientId(@Param("companyId") Long companyId,
                                                @Param("clientId") Long clientId);

    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId")
    Page<Client> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId AND c.clientState = :clientState")
    Page<Client> findByCompanyIdAndClientState(@Param("companyId") Long companyId,
                                               @Param("clientState") Boolean clientState,
                                               Pageable pageable);
}
