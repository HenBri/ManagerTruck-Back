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

    @Query("SELECT u FROM User u JOIN FETCH u.person p WHERE LOWER(p.email) = LOWER(:email) AND u.isDelete = false")
    Optional<User> findByPersonEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.personId = :personId AND u.isDelete = false")
    Optional<User> findByPersonId(@Param("personId") Long personId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.person p " +
            "WHERE LOWER(p.email) = LOWER(:email) AND u.isDelete = false")
    boolean existsByPersonEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.person p "
            + "WHERE u.userId = :userId AND u.isDelete = false")
    Optional<User> findActiveById(@Param("userId") Long userId);

    @Query(value = "SELECT r.role_name FROM user_role ur "
            + "INNER JOIN role_table r ON r.role_id = ur.role_id "
            + "WHERE ur.user_id = :userId AND ur.is_delete = false AND ur.state = 'ACTIVE'", nativeQuery = true)
    List<String> findRoleNames(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.isDelete = false")
    Page<User> findAllActive(Pageable pageable);
}
