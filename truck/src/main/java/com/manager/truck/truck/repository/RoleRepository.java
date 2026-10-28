package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.roleId = :roleId AND r.isDelete = false")
    Optional<Role> findActiveById(@Param("roleId") Integer roleId);

    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName AND r.isDelete = false")
    Optional<Role> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r " +
            "WHERE r.roleName = :roleName AND r.isDelete = false")
    boolean existsByRoleName(@Param("roleName") String roleName);

    @Query("SELECT r FROM Role r WHERE r.isDelete = false")
    Page<Role> findAllActive(Pageable pageable);
}
