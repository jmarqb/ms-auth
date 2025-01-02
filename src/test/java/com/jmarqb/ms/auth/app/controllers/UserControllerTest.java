package com.jmarqb.ms.auth.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.auth.app.dtos.request.SearchBodyDto;
import com.jmarqb.ms.auth.app.dtos.request.UpdateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.auth.app.enums.Gender;
import com.jmarqb.ms.auth.app.exceptions.UserNotFoundException;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.UserService;
import com.jmarqb.ms.auth.app.services.impl.JpaUserDetailsService;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.jmarqb.ms.auth.app.data.Data.createAdminUserResponseDto;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(username = "admin@email.com", authorities = "ADMIN")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JpaUserDetailsService jpaUserDetailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void search() throws Exception {
        SearchBodyDto searchBodyDto = SearchBodyDto.builder()
                .search("")
                .page(0)
                .size(10)
                .sort("ASC")
                .build();
        List<CreateUserResponseDto> list = List.of(createAdminUserResponseDto(1L), createAdminUserResponseDto(2L));

        PaginatedResponseDto paginatedResponseDto = PaginatedResponseDto.builder()
                .total(2)
                .page(0)
                .size(10)
                .data(list)
                .build();

        when(userService.search(searchBodyDto)).thenReturn(paginatedResponseDto);

        mockMvc.perform(post("/api/users/search").contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(searchBodyDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(paginatedResponseDto.getTotal()))
                .andExpect(jsonPath("$.page").value(paginatedResponseDto.getPage()))
                .andExpect(jsonPath("$.size").value(paginatedResponseDto.getSize()))
                .andExpect(jsonPath("$.data[0]").value(paginatedResponseDto.getData().get(0)))
                .andExpect(jsonPath("$.data[1]").value(paginatedResponseDto.getData().get(1)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(userService).search(searchBodyDto);
    }

    @Test
    void findUser() throws Exception {
        CreateUserResponseDto createUserResponseDto = createAdminUserResponseDto(1L);
        when(userService.findUser(1L)).thenReturn(createUserResponseDto);

        mockMvc.perform(get("/api/users/{id}",createUserResponseDto.getId()).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createUserResponseDto.getId()))
                .andExpect(jsonPath("$.email").value(createUserResponseDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(createUserResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createUserResponseDto.getLastName()))
                .andExpect(jsonPath("$.deleted").value(createUserResponseDto.isDeleted()));

        verify(userService).findUser(1L);
    }

    @Test
    void findUserIfNotExists() throws Exception {
        when(userService.findUser(1L)).thenThrow(new UserNotFoundException("The user does not exist"));


        mockMvc.perform(get("/api/users/1").contentType("application/json;charset=UTF-8"))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The user does not exist"));

        verify(userService).findUser(1L);

    }

    @Test
    void updateUser() throws Exception {
        CreateUserResponseDto createUserResponseDto = createAdminUserResponseDto(1L);
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("b4T0x@example.com")
                .phone("1234567890")
                .gender("MALE")
                .build();

        createUserResponseDto.setFirstName(updateUserDto.getFirstName());
        createUserResponseDto.setLastName(updateUserDto.getLastName());
        createUserResponseDto.setEmail(updateUserDto.getEmail());
        createUserResponseDto.setPhone(updateUserDto.getPhone());
        createUserResponseDto.setGender(Gender.valueOf(updateUserDto.getGender()));

        when(userService.updateUser(createUserResponseDto.getId(), updateUserDto)).thenReturn(createUserResponseDto);

        mockMvc.perform(patch("/api/users/{id}",createUserResponseDto.getId()).contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createUserResponseDto.getId()))
                .andExpect(jsonPath("$.email").value(createUserResponseDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(createUserResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createUserResponseDto.getLastName()))
                .andExpect(jsonPath("$.deleted").value(createUserResponseDto.isDeleted()));

        verify(userService).updateUser(1L, updateUserDto);
    }

    @Test
    void updateUserIfNotExists() throws Exception {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("b4T0x@example.com")
                .phone("1234567890")
                .gender("MALE")
                .build();

        when(userService.updateUser(1L, updateUserDto)).thenThrow(new UserNotFoundException("The user does not exist"));

        mockMvc.perform(patch("/api/users/1").contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The user does not exist"));

        verify(userService).updateUser(1L, updateUserDto);
    }

    @Test
    void removeUser() throws Exception {
        DeleteResponseDto response = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();
        when(userService.deleteUser(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/users/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(response.getDeletedCount()))
                .andExpect(jsonPath("$.acknowledged").value(response.isAcknowledged()));

        verify(userService).deleteUser(1L);
    }

    @Test
    void removeUserIfNotExists() throws Exception {
        when(userService.deleteUser(1L)).thenThrow(new UserNotFoundException("The user does not exist"));

        mockMvc.perform(delete("/api/users/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The user does not exist"));

        verify(userService).deleteUser(1L);
    }

}