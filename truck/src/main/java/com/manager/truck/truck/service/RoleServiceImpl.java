package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Roles;
import com.manager.truck.truck.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleRepository roleRepository;

    public Roles save(Roles roles) {
        return roleRepository.save(roles);
    }

    public Roles findById(Long id) {
        return roleRepository.getById(id);
    }

    public Roles update(Roles roles) {
        return roleRepository.save(roles);
    }

    public List<Roles> findAll() {
        return roleRepository.findAll();
    }
}
