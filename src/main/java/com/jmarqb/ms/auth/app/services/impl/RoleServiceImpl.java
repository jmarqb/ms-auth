package com.jmarqb.ms.auth.app.services.impl;

import com.jmarqb.ms.auth.app.dtos.request.CreateRoleDto;
import com.jmarqb.ms.auth.app.dtos.request.SearchBodyDto;
import com.jmarqb.ms.auth.app.dtos.request.UpdateRoleDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateRoleResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.auth.app.entities.Role;
import com.jmarqb.ms.auth.app.entities.RoleMapper;
import com.jmarqb.ms.auth.app.exceptions.RoleNotFoundException;
import com.jmarqb.ms.auth.app.repositories.RoleRepository;
import com.jmarqb.ms.auth.app.services.RoleService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }


    @Transactional
    @Override
    public CreateRoleResponseDto save(CreateRoleDto role) {
        Role newRole = roleMapper.toEntity(role);
        Role savedRole = roleRepository.save(newRole);
        return roleMapper.toResponse(savedRole);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedResponseDto search(SearchBodyDto searchBodyDto) {
        List<Role> roles;
        String search = searchBodyDto.getSearch();

        Pageable pageable = PageRequest.of(searchBodyDto.getPage(),
                searchBodyDto.getSize(),
                searchBodyDto.getSort().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, "id");

        roles = (search != null)
                ? roleRepository.searchAllByRegex(search, pageable)
                : roleRepository.searchAll(pageable);

        List<CreateRoleResponseDto> response = new ArrayList<>();
        roles.forEach(role -> response.add(roleMapper.toResponse(role)));

        return roleMapper.toPaginatedResponse(response, roles.size(), searchBodyDto.getPage(), searchBodyDto.getSize(), new Date());
    }


    @Transactional(readOnly = true)
    @Override
    public CreateRoleResponseDto findRole(Long id) {
        Role role = existsRole(id);
        return roleMapper.toResponse(role);
    }

    @Transactional
    @Override
    public CreateRoleResponseDto updateRole(Long id, UpdateRoleDto updateRoleDto) {
        Role role = existsRole(id);
        updateRoleFields(role, updateRoleDto);
        Role savedRole = roleRepository.save(role);

        return roleMapper.toResponse(savedRole);
    }

    @Transactional
    @Override
    public DeleteResponseDto deleteRole(Long id) {
        Role role = existsRole(id);
        role.setDeleted(true);
        role.setDeletedAt(new Date());
        roleRepository.save(role);

        return DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();
    }

    private Role existsRole(Long id) {
        Role role = roleRepository.findByIdAndDeletedFalse(id);
        if (role == null) {
            throw new RoleNotFoundException("The role does not exist");
        }
        return role;
    }

    private void updateRoleFields(Role role, UpdateRoleDto updateRoleDto) {
        if (updateRoleDto.getName() != null) role.setName(updateRoleDto.getName());
        if (updateRoleDto.getDescription() != null) role.setDescription(updateRoleDto.getDescription());
        if (updateRoleDto.getIcon() != null) role.setIcon(updateRoleDto.getIcon());
        if (updateRoleDto.getIsAdmin() != null) role.setAdmin(updateRoleDto.getIsAdmin());
        if (updateRoleDto.getIsDefaultRole() != null) role.setDefaultRole(updateRoleDto.getIsDefaultRole());
    }
}
