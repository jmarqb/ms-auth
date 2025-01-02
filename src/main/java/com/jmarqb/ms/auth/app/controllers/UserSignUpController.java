package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.dtos.request.CreateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.services.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "User Sign Up", description = "Endpoint for user sign up")
public class UserSignUpController {

    private final  UserService userService;

    public UserSignUpController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/signup")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    public ResponseEntity<CreateUserResponseDto> register(@Valid @RequestBody CreateUserDto createUserDto) {
        CreateUserResponseDto response = userService.save(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
