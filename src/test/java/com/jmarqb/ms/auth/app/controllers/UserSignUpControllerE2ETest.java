package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.data.seed.TestDataInitializer;
import com.jmarqb.ms.auth.app.dtos.request.CreateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.jmarqb.ms.auth.app.data.Data.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
public class UserSignUpControllerE2ETest {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private UserService userService;

    @LocalServerPort
    private int port;


    @Test
    void signUp() {
        CreateUserDto createUserDto = createUserDto();
        createUserDto.setEmail("newtestuser@example.com");
        createUserDto.setPhone("+789456123");
        CreateUserResponseDto createUserResponseDto = createUserResponseDto(2L);
        createUserResponseDto.setRoles(List.of(getUserRole(2L)));
        createUserResponseDto.setPhone(createUserDto.getPhone());
        createUserResponseDto.setEmail(createUserDto.getEmail());

        ResponseEntity<CreateUserResponseDto> response = client.postForEntity(createURI("/api/auth/signup"),
                createUserDto, CreateUserResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        CreateUserResponseDto body = response.getBody();

        assertNotNull(body);

        assertEquals(createUserResponseDto.getFirstName(), body.getFirstName());
        assertEquals(createUserResponseDto.getLastName(), body.getLastName());
        assertEquals(createUserResponseDto.getEmail(), body.getEmail());
        assertEquals(createUserResponseDto.getRoles(), body.getRoles());
        assertEquals(createUserResponseDto.getId(), body.getId());
        assertEquals(createUserResponseDto.getDeletedAt(), body.getDeletedAt());
        assertEquals(createUserResponseDto.isDeleted(), body.isDeleted());
        assertEquals(createUserResponseDto.getGender(), body.getGender());
        assertEquals(createUserResponseDto.getCountry(), body.getCountry());
        assertEquals(createUserResponseDto.getPhone(), body.getPhone());

    }

    @Test
    void signUpThrowBadRequest() {
        CreateUserDto createUserDto = createUserDto();
        createUserDto.setFirstName(null);

        ResponseEntity<Error> response = client.postForEntity(createURI("/api/auth/signup"),
                createUserDto, Error.class);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Error error = response.getBody();

        assertNotNull(error);

        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Bad Request", error.getError());
        assertEquals("Validation failed", error.getMessage());
        assertEquals("firstName", error.getFieldErrors().get(0).getField());
        assertEquals("null", error.getFieldErrors().get(0).getRejectedValue());
//        assertEquals("firstName is required", error.getFieldErrors().get(0).getMessage());
    }

    private String createURI(String uri) {
        return "http://localhost:" + port + uri;
    }

}
