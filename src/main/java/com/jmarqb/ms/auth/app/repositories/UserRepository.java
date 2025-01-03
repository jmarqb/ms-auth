package com.jmarqb.ms.auth.app.repositories;

import com.jmarqb.ms.auth.app.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    @Query("SELECT r FROM User r WHERE r.deleted = false")
    List<User> searchAll(Pageable pageable);

    @Query("SELECT r FROM User r WHERE r.deleted = false AND lower(r.email) LIKE lower(concat('%', ?1, '%')) " +
            "OR lower(r.firstName) LIKE lower(concat('%', ?1, '%')) OR lower(r.lastName) LIKE lower(concat('%', ?1, '%'))")
    List<User> searchAllByRegex(String regex, Pageable pageable);

    User findByIdAndDeletedFalse(Long id);

    @Query("SELECT r FROM User r WHERE r.deleted = false AND r.email = ?1")
    Optional<User> findByUsername(String username);


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}
