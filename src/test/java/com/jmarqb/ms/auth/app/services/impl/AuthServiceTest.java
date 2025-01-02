package com.jmarqb.ms.auth.app.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jmarqb.ms.auth.app.dtos.request.LoginDto;
import com.jmarqb.ms.auth.app.dtos.response.AuthResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginValidCredentials() throws JsonProcessingException {
        LoginDto loginDto = new LoginDto("admin@test.com", "1234");
        User mockUser = mock(User.class);

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ))).thenReturn(mockAuthentication);

        String mockToken = "eyJhbGciOiJIUzI1NiJ9.mock.jwt.token";
        when(jwtService.generateToken((org.springframework.security.core.userdetails.User)mockUser)).thenReturn(mockToken);

        AuthResponseDto response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals(mockToken, response.getToken());

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    void loginThrowBadCredentialsException() {
        LoginDto loginDto = new LoginDto("invalid@test.com", "wrong_password");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginDto));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginThrowJsonProcessingException() throws JsonProcessingException {
        LoginDto loginDto = new LoginDto("admin@test.com", "1234");
        User mockUser = mock(User.class);

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ))).thenReturn(mockAuthentication);

        when(jwtService.generateToken(mockUser)).thenThrow(new JsonProcessingException("Error generating token") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginDto));
        assertEquals("Error generating token", exception.getCause().getMessage());


        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtService).generateToken(mockUser);
    }
}