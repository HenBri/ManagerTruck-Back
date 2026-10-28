package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.UserRole;
import com.manager.truck.truck.domain.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.id.userId = :userId AND ur.isDelete = false")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
            "WHERE ur.id.userId = :userId AND ur.id.roleId = :roleId AND ur.isDelete = false")
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Integer roleId);
}

