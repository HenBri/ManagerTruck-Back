package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person update(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person findById(Long personId) {
        return personRepository.findActiveById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id: " + personId));
    }

    @Override
    public Optional<Person> findOptionalById(Long personId) {
        return personRepository.findActiveById(personId);
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndPersonIdNot(String email, Long personId) {
        return personRepository.existsByEmailAndPersonIdNot(email, personId);
    }

    @Override
    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAllActive(pageable);
    }
}

