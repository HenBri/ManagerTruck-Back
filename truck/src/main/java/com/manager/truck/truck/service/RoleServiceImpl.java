package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findById(Integer roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleId));
    }

    @Override
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName));
    }

    @Override
    public Role update(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "roleId"));
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }
}
