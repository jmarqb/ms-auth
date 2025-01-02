package com.jmarqb.ms.auth.app.entities;

import com.jmarqb.ms.auth.app.dtos.request.CreateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(CreateUserDto createUserDto);

    @Mapping(target = "id", ignore = false)
    CreateUserResponseDto toResponse(User user);

    @Mapping(target = "isAdmin", source = "admin")
    @Mapping(target = "isDefaultRole", source = "defaultRole")
    UserRole map(Role role);

    @Mapping(target = "data", source = "users")
    PaginatedResponseDto toPaginatedResponse(List<?> users, int total, int page, int size, Date timestamp);

}
