package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.PlatformUserRole;
import com.manager.truck.truck.repository.PlatformUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformUserRoleServiceImpl implements PlatformUserRoleService {

    private final PlatformUserRoleRepository platformUserRoleRepository;

    @Override
    public List<PlatformUserRole> findAll() {
        return platformUserRoleRepository.findAllOrdered();
    }
}
