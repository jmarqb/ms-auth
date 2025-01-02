package com.jmarqb.ms.auth.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jmarqb.ms.auth.app.data.seed.TestDataInitializer;
import com.jmarqb.ms.auth.app.dtos.request.LoginDto;
import com.jmarqb.ms.auth.app.dtos.response.AuthResponseDto;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.entities.User;
import com.jmarqb.ms.auth.app.repositories.UserRepository;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
class AuthControllerE2ETest {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private String token;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() throws JsonProcessingException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                "testadmin@example.com",
                "password",
                true,
                true,
                true,
                true,
                authorities
        );
        token = configureJwtToken(user);
        client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        });
    }

    protected String configureJwtToken(org.springframework.security.core.userdetails.User user) throws JsonProcessingException {
        return jwtService.generateToken(user);
    }

    @Test
    void login() {
        Optional<User> user = userRepository.findByEmail("testadmin@example.com");
        LoginDto loginDto = new LoginDto("testadmin@example.com", "password");

        ResponseEntity<AuthResponseDto> response = client.postForEntity(createURI("/api/auth/login"), loginDto, AuthResponseDto.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(jwtService.isTokenValid(response.getBody().getToken(), user.get()));
    }

    @Test
    void loginBadCredentials(){
        LoginDto loginDto = new LoginDto("testadmin@example.com", "badpassword");
        ResponseEntity<Error> response =
                client.postForEntity(createURI("/api/auth/login"), loginDto, Error.class);
        assertEquals(401, response.getStatusCode().value());
    }

    private String createURI(String uri) {
        return "http://localhost:" + port + uri;
    }
}
