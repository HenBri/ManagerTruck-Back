package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PersonService {

    Person save(Person person);

    Person update(Person person);

    Person findById(Long personId);

    Optional<Person> findOptionalById(Long personId);

    Optional<Person> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndPersonIdNot(String email, Long personId);

    Page<Person> findAll(Pageable pageable);
}

