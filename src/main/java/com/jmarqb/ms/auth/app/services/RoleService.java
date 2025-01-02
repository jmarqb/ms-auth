package com.jmarqb.ms.auth.app.services;

import com.jmarqb.ms.auth.app.dtos.request.CreateRoleDto;
import com.jmarqb.ms.auth.app.dtos.request.SearchBodyDto;
import com.jmarqb.ms.auth.app.dtos.request.UpdateRoleDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateRoleResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;

public interface RoleService {

    CreateRoleResponseDto save(CreateRoleDto role);

    PaginatedResponseDto search(SearchBodyDto searchBodyDto);

    CreateRoleResponseDto findRole(Long id);

    CreateRoleResponseDto updateRole(Long id, UpdateRoleDto updateRoleDto);

    DeleteResponseDto deleteRole(Long id);

}
