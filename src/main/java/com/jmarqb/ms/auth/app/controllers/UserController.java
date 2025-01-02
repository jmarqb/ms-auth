package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.dtos.request.*;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.OpenApiResponses;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.services.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name= "User Management", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/search")
    @ApiResponse(responseCode = "200", description = "Users found successfully")
    @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.BAD_REQUEST_EXAMPLE)))
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
                    )))
    public ResponseEntity<PaginatedResponseDto> search(@Valid @RequestBody SearchBodyDto searchBodyDto) {
        PaginatedResponseDto response = userService.search(searchBodyDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "User found successfully")
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
                    )))
    public ResponseEntity<CreateUserResponseDto> findUser(@PathVariable Long id) {
        CreateUserResponseDto response = userService.findUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
    @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.BAD_REQUEST_EXAMPLE)))
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
                    )))
    public ResponseEntity<CreateUserResponseDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        CreateUserResponseDto response = userService.updateUser(id, updateUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "User deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
                    )))
    public ResponseEntity<DeleteResponseDto> removeUser(@PathVariable Long id) {
        DeleteResponseDto response = userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
