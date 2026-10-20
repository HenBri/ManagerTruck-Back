package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Role;

import java.util.List;

public interface RoleService {

    Role save(Role role);

    Role findById(Integer roleId);

    Role findByRoleName(String roleName);

    Role update(Role role);

    List<Role> findAll();

    boolean existsByRoleName(String roleName);
}
