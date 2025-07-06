package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Roles;

import java.util.List;

public interface RoleService {
    Roles save(Roles roles);
    Roles findById(Long id);
    Roles update(Roles roles);
    List<Roles> findAll();
}
