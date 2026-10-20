package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.PlatformUserRole;
import com.manager.truck.truck.domain.PlatformUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlatformUserRoleRepository extends JpaRepository<PlatformUserRole, PlatformUserRoleId> {

    @Query("SELECT pur FROM PlatformUserRole pur ORDER BY pur.id.userId ASC, pur.id.roleId ASC")
    List<PlatformUserRole> findAllOrdered();
}
