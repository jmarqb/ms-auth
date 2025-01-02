package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.dtos.request.LoginDto;
import com.jmarqb.ms.auth.app.dtos.response.AuthResponseDto;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.services.impl.AuthService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "User login", description = "Endpoint for user login")
public class AuthController {
    private final AuthService authService;

    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto loginRequest) {
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

