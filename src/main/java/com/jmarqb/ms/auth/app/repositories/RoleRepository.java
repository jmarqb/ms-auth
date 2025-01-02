package com.jmarqb.ms.auth.app.repositories;

import com.jmarqb.ms.auth.app.entities.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.deleted = false")
    List<Role> searchAll(Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.deleted = false AND lower(r.name) LIKE lower(concat('%', ?1, '%'))")
    List<Role> searchAllByRegex(String name, Pageable pageable);

    Role findByIdAndDeletedFalse(Long id);

    @Query("SELECT r FROM Role r WHERE r.deleted = false AND r.name = ?1")
    Optional<Role> findByName(String name);

}
