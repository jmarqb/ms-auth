package com.jmarqb.ms.auth.app.jpatest;

import com.jmarqb.ms.auth.app.entities.Role;
import com.jmarqb.ms.auth.app.entities.User;
import com.jmarqb.ms.auth.app.repositories.RoleRepository;
import org.instancio.Instancio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Transactional
public class RoleJpaTest {

    private @Autowired RoleRepository roleRepository;
    private Role roleAdmin;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();

        List<User> users = new ArrayList<>();

        roleAdmin = Instancio.of(Role.class)
                .set(field(Role::getId), 1L)
                .set(field(Role::getName), "ADMIN")
                .set(field(Role::getDeletedAt), null)
                .set(field(Role::isDeleted), false)
                .set(field(Role::getUsers), users)
                .create();

        roleUser = Instancio.of(Role.class)
                .set(field(Role::getId), 2L)
                .set(field(Role::getName), "USER")
                .set(field(Role::isAdmin), false)
                .set(field(Role::getDeletedAt), null)
                .set(field(Role::isDeleted), false)
                .set(field(Role::getUsers), users)
                .create();
        List<Role> randomRoles = Instancio.ofList(Role.class)
                .size(3)
                .generate(field(Role::getName), gen -> gen.text().pattern("ROLE_####"))
                .generate(field(Role::isDeleted), generators -> generators.booleans().probability(1))
                .generate(field(Role::getUsers), generators -> generators.collection().size(0))
                .create();
        roleRepository.saveAll(List.of(roleAdmin, roleUser));
        roleRepository.saveAll(randomRoles);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void save() {
        Role role = Instancio.of(Role.class)
                .set(field(Role::getDeletedAt), null)
                .set(field(Role::isDeleted), false)
                .set(field(Role::getUsers), new ArrayList<>())
                .create();
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole).usingRecursiveComparison().ignoringFields("id").isEqualTo(role);
    }

    @Test
    void searchAllByDeletedFalseAndNameContainsIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.ASC, "id");

        List<Role> roles = roleRepository.searchAllByRegex("u", pageable);
        System.out.println("roles = " + roles);
        assertTrue(roles.size() > 0);
        assertThat(roles.get(0)).usingRecursiveComparison().ignoringFields("id").isEqualTo(roleUser);

    }

    @Test
    void searchAllByDeletedFalse() {
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.ASC, "id");

        List<Role> roles = roleRepository.searchAll(pageable);

        assertEquals(2, roles.size());
        assertThat(roles.get(0)).usingRecursiveComparison().ignoringFields("id").isEqualTo(roleAdmin);
        assertThat(roles.get(1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(roleUser);
    }

    @Test
    void findByIdAndDeletedFalse() {
        Role role = roleRepository.findByIdAndDeletedFalse(roleAdmin.getId());
        assertThat(role).usingRecursiveComparison().isEqualTo(roleAdmin);
    }

    @Test
    void findByIdAndDeletedFalseNull() {
        roleRepository.delete(roleAdmin);
        Role role = roleRepository.findByIdAndDeletedFalse(roleAdmin.getId());
        assertThat(role).usingRecursiveComparison().isEqualTo(null);

    }
}
