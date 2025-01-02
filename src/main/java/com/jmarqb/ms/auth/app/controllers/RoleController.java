package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.dtos.request.*;
import com.jmarqb.ms.auth.app.dtos.response.*;
import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.services.RoleService;
import io.swagger.v3.oas.annotations.media.*;
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
@Tag(name = "Role Management", description = "Endpoints for managing roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @PostMapping
    @ApiResponse(responseCode = "201", description = "Role created successfully")
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
    public ResponseEntity<CreateRoleResponseDto> create(@Valid @RequestBody CreateRoleDto createRoleDto) {
        CreateRoleResponseDto response = roleService.save(createRoleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search")
    @ApiResponse(responseCode = "200", description = "Roles found successfully")
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
        PaginatedResponseDto response = roleService.search(searchBodyDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Role found successfully")
    @ApiResponse(responseCode = "404", description = "Role not found",
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
    public ResponseEntity<CreateRoleResponseDto> findRole(@PathVariable Long id) {
        CreateRoleResponseDto response = roleService.findRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    @ApiResponse(responseCode = "404", description = "Role not found",
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
    public ResponseEntity<CreateRoleResponseDto> updateRole(@PathVariable Long id, @RequestBody UpdateRoleDto updateRoleDto) {
        log.info("Finding role with id: {}", id);
        CreateRoleResponseDto response = roleService.updateRole(id, updateRoleDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Role deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Role not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.ENTITY_NOT_FOUND_EXAMPLE)))
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Error.class),
                    examples = @ExampleObject(value = OpenApiResponses.UNAUTHORIZED_EXAMPLE
                    )))
    public ResponseEntity<DeleteResponseDto> removeRole(@PathVariable Long id) {
        DeleteResponseDto response = roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
