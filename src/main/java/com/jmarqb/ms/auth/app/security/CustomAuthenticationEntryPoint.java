package com.jmarqb.ms.auth.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.auth.app.entities.Error;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Date;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Error error = Error.builder()
                    .timestamp(new Date())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Unauthorized")
                    .build();

            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
