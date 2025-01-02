package com.jmarqb.ms.auth.app.services;

import com.jmarqb.ms.auth.app.dtos.request.*;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;

public interface UserService {

    CreateUserResponseDto save(CreateUserDto user);

    PaginatedResponseDto search(SearchBodyDto searchBodyDto);

    CreateUserResponseDto findUser(Long id);


    CreateUserResponseDto updateUser(Long id, UpdateUserDto updateUserDto);

    DeleteResponseDto deleteUser(Long id);

    PaginatedResponseDto addRoleToManyUsers(RoleToUsersDto roleToUsersDto);

    PaginatedResponseDto removeRoleToManyUsers(RoleToUsersDto roleToUsersDto);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
