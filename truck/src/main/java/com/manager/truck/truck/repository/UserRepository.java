package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);

    @Query(value = "SELECT r.role_name FROM platform_user_role pur "
            + "INNER JOIN role_table r ON r.role_id = pur.role_id AND r.role_scope = pur.role_scope "
            + "WHERE pur.user_id = :userId AND pur.role_scope = 'PLATFORM'", nativeQuery = true)
    List<String> findPlatformRoleNames(@Param("userId") Long userId);

    @Query(value = "SELECT DISTINCT r.role_name FROM company_user_role cur "
            + "INNER JOIN company_user cu ON cu.company_id = cur.company_id AND cu.user_id = cur.user_id AND cu.membership_status = 'ACTIVE' "
            + "INNER JOIN role_table r ON r.role_id = cur.role_id AND r.role_scope = cur.role_scope "
            + "WHERE cur.user_id = :userId AND cur.role_scope = 'COMPANY'", nativeQuery = true)
    List<String> findAllCompanyRoleNames(@Param("userId") Long userId);

    @Query(value = "SELECT DISTINCT r.role_name FROM company_user_role cur "
            + "INNER JOIN company_user cu ON cu.company_id = cur.company_id AND cu.user_id = cur.user_id AND cu.membership_status = 'ACTIVE' "
            + "INNER JOIN role_table r ON r.role_id = cur.role_id AND r.role_scope = cur.role_scope "
            + "WHERE cur.user_id = :userId AND cur.company_id = :companyId AND cur.role_scope = 'COMPANY'", nativeQuery = true)
    List<String> findCompanyRoleNames(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query(value = "SELECT COUNT(*) FROM company_user cu "
            + "WHERE cu.company_id = :companyId AND cu.user_id = :userId AND cu.membership_status = 'ACTIVE'", nativeQuery = true)
    long countActiveMembership(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query("SELECT u FROM User u WHERE EXISTS ("
            + "SELECT cu FROM CompanyUser cu "
            + "WHERE cu.id.userId = u.userId "
            + "AND cu.id.companyId = :companyId "
            + "AND cu.membershipStatus = com.manager.truck.truck.domain.enums.MembershipStatus.ACTIVE)")
    Page<User> findCompanyMembers(@Param("companyId") Long companyId, Pageable pageable);
}
