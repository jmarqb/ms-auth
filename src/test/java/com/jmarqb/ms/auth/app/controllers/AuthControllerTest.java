package com.jmarqb.ms.auth.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.auth.app.dtos.request.LoginDto;
import com.jmarqb.ms.auth.app.dtos.response.AuthResponseDto;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.impl.AuthService;
import com.jmarqb.ms.auth.app.services.impl.JpaUserDetailsService;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class)
@Import(SpringSecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @MockBean
    JwtService jwtService;

    @MockBean
    JpaUserDetailsService jpaUserDetailsService;


    @Test
    void login() throws Exception {
        String mockToken = "eyJhbGciOiJIUzI1NiJ9.mock.jwt.token";
        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken(mockToken);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("testuser@example.com");
        loginDto.setPassword("testpassword");

        when(authService.login(any(LoginDto.class))).thenReturn(responseDto);


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(responseDto.getToken()));

        verify(authService).login(any(LoginDto.class));
    }
}