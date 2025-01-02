package com.jmarqb.ms.auth.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.auth.app.dtos.request.CreateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.UserService;
import com.jmarqb.ms.auth.app.services.impl.JpaUserDetailsService;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.jmarqb.ms.auth.app.data.Data.createUserDto;
import static com.jmarqb.ms.auth.app.data.Data.createAdminUserResponseDto;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserSignUpController.class)
@Import(SpringSecurityConfig.class)
class UserSignUpControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    JwtService jwtService;

    @MockBean
    JpaUserDetailsService jpaUserDetailsService;

    @Test
    void register() throws Exception {
        CreateUserDto createUserDto = createUserDto();
        CreateUserResponseDto createUserResponseDto = createAdminUserResponseDto(1L);
        when(userService.save(createUserDto)).thenReturn(createUserResponseDto);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createUserResponseDto.getId()))
                .andExpect(jsonPath("$.email").value(createUserResponseDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(createUserResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createUserResponseDto.getLastName()))
                .andExpect(jsonPath("$.deleted").value(createUserResponseDto.isDeleted()));

        verify(userService).save(createUserDto);
    }
}