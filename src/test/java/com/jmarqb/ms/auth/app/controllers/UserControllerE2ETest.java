package com.jmarqb.ms.auth.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jmarqb.ms.auth.app.data.seed.TestDataInitializer;
import com.jmarqb.ms.auth.app.dtos.request.CreateUserDto;
import com.jmarqb.ms.auth.app.dtos.request.SearchBodyDto;
import com.jmarqb.ms.auth.app.dtos.request.UpdateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.repositories.UserRepository;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.jmarqb.ms.auth.app.data.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
public class UserControllerE2ETest {

    @Autowired
    private TestRestTemplate client;

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
    @Transactional
    @Order(0)
    void create() {
        CreateUserResponseDto expected =  createUserResponseDto(2L);
        expected.setPhone("+78945612355");
        CreateUserDto createUserDto = createUserDto();
        createUserDto.setPhone("+78945612355");


        ResponseEntity<CreateUserResponseDto> newUser = client.postForEntity(createURI("/api/auth/signup"),
                createUserDto,
                CreateUserResponseDto.class);

        checkedResponseEntity(newUser, expected, HttpStatus.CREATED);
    }

    @Test
    @Order(1)
    void search() {
        SearchBodyDto searchBodyDto = createSearchBodyDto("ad", 0, 10, "ASC");

        ResponseEntity<PaginatedResponseDto> response = client.postForEntity(createURI("/api/users/search"),
                searchBodyDto, PaginatedResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        PaginatedResponseDto body = response.getBody();

        assertNotNull(body);
        assertEquals(1, body.getData().size());
        assertEquals(0, body.getPage());
        assertEquals(10, body.getSize());
        assertEquals(1, body.getTotal());
    }

    @Test
    @Order(2)
    void findUser() {
        CreateUserResponseDto expected = createAdminUserResponseDto(1L);
        ResponseEntity<CreateUserResponseDto> response = client.getForEntity
                (createURI(String.format("/api/users/%d", (1L))),
                        CreateUserResponseDto.class);

        checkedResponseEntity(response, expected, HttpStatus.OK);
    }

    @Test
    @Order(3)
    void findUserIfNotExist() {

        ResponseEntity<Error> response = client.getForEntity(createURI("/api/users/10000"),
                Error.class);

        checkedErrorResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(4)
    void updateUserIfNotExist() {

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .age(30)
                .phone("+100000000")
                .gender("MALE")
                .country("Testland")
                .build();

        HttpEntity<UpdateUserDto> entity = new HttpEntity<>(updateUserDto);


        ResponseEntity<Error> response = client.exchange(
                createURI(String.format("/api/users/%d", 10000L)),
                PATCH,
                entity,
                Error.class
        );

        checkedErrorResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(6)
    void updateUser() {
        CreateUserResponseDto expected =  createUserResponseDto(1L);
        expected.setEmail("testuser1@example.com");

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser1@example.com")
                .age(30)
                .phone("+100000000")
                .gender("MALE")
                .country("Testland")
                .build();

        HttpEntity<UpdateUserDto> entity = new HttpEntity<>(updateUserDto);


        ResponseEntity<CreateUserResponseDto> response = client.exchange(
                createURI(String.format("/api/users/%d", 1L)),
                PATCH,
                entity,
                CreateUserResponseDto.class
        );

        checkedResponseEntity(response, expected, HttpStatus.OK);
    }

    @Test
    @Order(5)
    void removeUser() {
        ResponseEntity<DeleteResponseDto> response = client.exchange(
                createURI(String.format("/api/users/%d", 2L)),
                DELETE,
                new HttpEntity<>(null),
                DeleteResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        DeleteResponseDto body = response.getBody();

        assertNotNull(body);
        assertEquals(1, body.getDeletedCount());
        assertTrue(body.isAcknowledged());

        ResponseEntity<Error> findUserAfterDelete = client.getForEntity(
                createURI(String.format("/api/users/%d", 2L)),
                Error.class);
        checkedErrorResponseEntity(findUserAfterDelete, HttpStatus.NOT_FOUND);

    }


    private void checkedResponseEntity(ResponseEntity<CreateUserResponseDto> response, CreateUserResponseDto expected,
                                       HttpStatus status) {
        assertEquals(status, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        CreateUserResponseDto body = response.getBody();

        assertNotNull(body);
        assertEquals(expected.getFirstName(), body.getFirstName());
        assertEquals(expected.getLastName(), body.getLastName());
        assertEquals(expected.getEmail(), body.getEmail());
        assertEquals(expected.getPhone(), body.getPhone());
        assertEquals(expected.getGender(), body.getGender());
        assertEquals(expected.getCountry(), body.getCountry());
        assertEquals(expected.isDeleted(), body.isDeleted());
        assertEquals(expected.getDeletedAt(), body.getDeletedAt());
        assertEquals(expected.getId(), body.getId());
    }

    public void checkedErrorResponseEntity(ResponseEntity<Error> response, HttpStatus status) {
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Error body = response.getBody();

        assertNotNull(body);

        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("NOT FOUND", body.getError());
        assertEquals("The user does not exist", body.getMessage());
    }
    private String createURI(String uri) {
        return "http://localhost:" + port + uri;
    }
}
