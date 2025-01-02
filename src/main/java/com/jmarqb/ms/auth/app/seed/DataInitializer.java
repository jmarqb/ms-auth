package com.jmarqb.ms.auth.app.seed;

import com.jmarqb.ms.auth.app.entities.Role;
import com.jmarqb.ms.auth.app.entities.User;
import com.jmarqb.ms.auth.app.enums.Gender;
import com.jmarqb.ms.auth.app.repositories.RoleRepository;
import com.jmarqb.ms.auth.app.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private Role adminRole;
    private Role userRole;

    public DataInitializer(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public Role getAdminRole() {
        return adminRole;
    }

    public Role getUserRole() {
        return userRole;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("USER").isEmpty()) {
            userRole = roleRepository.save(
                    Role.builder()
                            .name("USER")
                            .description("ROLE_USER")
                            .isDefaultRole(true)
                            .isAdmin(false)
                            .deleted(false)
                            .build());
        } else {
            userRole = roleRepository.findByName("USER").orElseThrow();
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            adminRole = roleRepository.save(
                    Role.builder()
                            .name("ADMIN")
                            .description("ROLE_ADMIN")
                            .isDefaultRole(false)
                            .isAdmin(true)
                            .deleted(false)
                            .build());
        } else {
            adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        }

        if (userRepository.findByUsername("admin@example.com").isEmpty()) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .email("admin@example.com")
                    .age(20)
                    .password("password")
                    .phone("+1234567890")
                    .gender(Gender.MALE)
                    .country("Cuba")
                    .roles(new ArrayList<>()).build();
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            admin.getRoles().add(this.getAdminRole());
            admin.getRoles().add(this.getUserRole());
            userRepository.save(admin);
        }
    }
}

