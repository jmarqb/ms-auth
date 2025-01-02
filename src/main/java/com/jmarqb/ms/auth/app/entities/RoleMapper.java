package com.jmarqb.ms.auth.app.entities;

import com.jmarqb.ms.auth.app.dtos.request.CreateRoleDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateRoleResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toEntity(CreateRoleDto createRoleDto);

    @Mapping(target = "id", ignore = false)
    @Mapping(target = "isAdmin", source = "admin")
    @Mapping(target = "isDefaultRole", source = "defaultRole")
    CreateRoleResponseDto toResponse(Role role);

    @Mapping(target = "data", source = "roles")
    PaginatedResponseDto toPaginatedResponse(List<?> roles, int total, int page, int size, Date timestamp);

}
