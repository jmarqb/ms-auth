package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.dtos.request.RoleToUsersDto;
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
@RequestMapping("/api/roles")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Roles Users")
public class RoleUserController {

    private final UserService userService;

    public RoleUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add/to-many-users")
    @ApiResponse(responseCode = "200", description = "Role added to many users successfully")
    @ApiResponse(responseCode = "404", description = "Role not found or user not found",
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
    public ResponseEntity<PaginatedResponseDto> addRoleToManyUsers(@Valid @RequestBody RoleToUsersDto roleToUsersDto) {
        PaginatedResponseDto response = userService.addRoleToManyUsers(roleToUsersDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/remove/to-many-users")
    @ApiResponse(responseCode = "200", description = "Role added to many users successfully")
    @ApiResponse(responseCode = "404", description = "Role not found or user not found",
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
    public ResponseEntity<PaginatedResponseDto> removeRoleToManyUsers(@Valid @RequestBody RoleToUsersDto roleToUsersDto) {
        PaginatedResponseDto response = userService.removeRoleToManyUsers(roleToUsersDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
