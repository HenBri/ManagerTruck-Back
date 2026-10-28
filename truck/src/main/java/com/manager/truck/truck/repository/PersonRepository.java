package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p WHERE p.personId = :personId AND p.isDelete = false")
    Optional<Person> findActiveById(@Param("personId") Long personId);

    @Query("SELECT p FROM Person p WHERE LOWER(p.email) = LOWER(:email) AND p.isDelete = false")
    Optional<Person> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Person p " +
            "WHERE LOWER(p.email) = LOWER(:email) AND p.isDelete = false")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Person p " +
            "WHERE LOWER(p.email) = LOWER(:email) AND p.personId <> :personId AND p.isDelete = false")
    boolean existsByEmailAndPersonIdNot(@Param("email") String email, @Param("personId") Long personId);

    @Query("SELECT p FROM Person p WHERE p.isDelete = false")
    Page<Person> findAllActive(Pageable pageable);
}

