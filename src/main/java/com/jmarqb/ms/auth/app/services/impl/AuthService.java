package com.jmarqb.ms.auth.app.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jmarqb.ms.auth.app.dtos.request.LoginDto;
import com.jmarqb.ms.auth.app.dtos.response.AuthResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public AuthResponseDto login(LoginDto loginRequest){
        {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(),
                                loginRequest.getPassword()
                        )
                );
                User user = (User) authentication.getPrincipal();
                return new AuthResponseDto(jwtService.generateToken(user));

            } catch (BadCredentialsException e) {
                throw new BadCredentialsException("Invalid email or password", e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error generating token", e);
            }
        }
    }
}

