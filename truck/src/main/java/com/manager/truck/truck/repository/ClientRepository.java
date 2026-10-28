package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.clientId = :clientId AND c.isDelete = false")
    Optional<Client> findActiveById(@Param("clientId") Long clientId);

    @Query("SELECT c FROM Client c WHERE c.isDelete = false")
    Page<Client> findAllActive(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c " +
            "WHERE LOWER(c.clientName) = LOWER(:clientName) AND c.isDelete = false")
    boolean existsByClientName(@Param("clientName") String clientName);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c " +
            "WHERE LOWER(c.clientName) = LOWER(:clientName) AND c.clientId <> :clientId AND c.isDelete = false")
    boolean existsByClientNameAndClientIdNot(@Param("clientName") String clientName, @Param("clientId") Long clientId);
}
