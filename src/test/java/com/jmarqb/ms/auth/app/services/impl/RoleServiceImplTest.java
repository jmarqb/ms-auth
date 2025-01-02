package com.jmarqb.ms.auth.app.services.impl;

import com.jmarqb.ms.auth.app.dtos.request.*;
import com.jmarqb.ms.auth.app.dtos.response.*;
import com.jmarqb.ms.auth.app.entities.*;
import com.jmarqb.ms.auth.app.exceptions.RoleNotFoundException;
import com.jmarqb.ms.auth.app.repositories.RoleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static com.jmarqb.ms.auth.app.data.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    private RoleServiceImpl roleService;

    @BeforeEach
    void setup() {
        roleService = new RoleServiceImpl(roleMapper);
        ReflectionTestUtils.setField(roleService, "roleRepository", roleRepository);
    }


    @Test
    void save() {
        CreateRoleDto createRoleDto = createRoleAdmin();
        CreateRoleResponseDto response = getRoleAdmin(1L);
        Role role = Role.builder()
                .id(response.getId())
                .name(createRoleDto.getName())
                .description(createRoleDto.getDescription())
                .icon(createRoleDto.getIcon())
                .isAdmin(createRoleDto.getIsAdmin())
                .isDefaultRole(createRoleDto.getIsDefaultRole())
                .deleted(response.isDeleted())
                .deletedAt(response.getDeletedAt())
                .build();

        when(roleMapper.toEntity(createRoleDto)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(response);


        CreateRoleResponseDto savedRole = roleService.save(createRoleDto);

        assertEquals(response.getId(), savedRole.getId());
        assertEquals(response.getName(), savedRole.getName());
        assertEquals(response.getDescription(), savedRole.getDescription());
        assertEquals(response.getIcon(), savedRole.getIcon());
        assertEquals(response.getIsAdmin(), savedRole.getIsAdmin());
        assertEquals(response.getIsDefaultRole(), savedRole.getIsDefaultRole());
        assertEquals(response.isDeleted(), savedRole.isDeleted());
        assertEquals(response.getDeletedAt(), savedRole.getDeletedAt());

        verify(roleMapper).toEntity(createRoleDto);
        verify(roleRepository).save(any(Role.class));
        verify(roleMapper).toResponse(role);
    }

    @Test
    void searchContainsRegex() {
        SearchBodyDto searchBodyDto = createSearchBodyDto("a", 0, 10, "ASC");

        List<Role> list = List.of(createRole(1L), createRole(2L));
        List<CreateRoleResponseDto> list1 = List.of(getRoleUser(1L), getRoleUser(2L));

        PaginatedResponseDto paginatedResponseDto = PaginatedResponseDto.builder()
                .total(list.size())
                .page(searchBodyDto.getPage())
                .size(searchBodyDto.getSize())
                .data(list)
                .build();

        Pageable pageable = PageRequest.of(searchBodyDto.getPage(),
                searchBodyDto.getSize(),
                searchBodyDto.getSort().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, "id");

        when(roleRepository.searchAllByRegex(searchBodyDto.getSearch(), pageable)).thenReturn(list);
        when(roleMapper.toResponse(list.get(0))).thenReturn(list1.get(0));
        when(roleMapper.toResponse(list.get(1))).thenReturn(list1.get(1));
        when(roleMapper.toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class)))
                .thenReturn(paginatedResponseDto);


        PaginatedResponseDto response = roleService.search(searchBodyDto);

        assertEquals(response.getTotal(), paginatedResponseDto.getTotal());
        assertEquals(response.getPage(), paginatedResponseDto.getPage());
        assertEquals(response.getSize(), paginatedResponseDto.getSize());
        assertEquals(response.getData(), paginatedResponseDto.getData());

        verify(roleRepository).searchAllByRegex(searchBodyDto.getSearch(), pageable);
        verify(roleMapper).toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class));
    }

    @Test
    void searchNotRegex() {
        SearchBodyDto searchBodyDto = createSearchBodyDto(null, 0, 10, "DESC");

        List<Role> list = List.of(createRole(1L), createRole(2L));
        List<CreateRoleResponseDto> list1 = List.of(getRoleUser(1L), getRoleUser(2L));

        PaginatedResponseDto paginatedResponseDto = PaginatedResponseDto.builder()
                .total(list.size())
                .page(searchBodyDto.getPage())
                .size(searchBodyDto.getSize())
                .data(list)
                .build();

        Pageable pageable = PageRequest.of(searchBodyDto.getPage(),
                searchBodyDto.getSize(),
                searchBodyDto.getSort().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, "id");

        when(roleRepository.searchAll(pageable)).thenReturn(list);
        when(roleMapper.toResponse(list.get(0))).thenReturn(list1.get(0));
        when(roleMapper.toResponse(list.get(1))).thenReturn(list1.get(1));
        when(roleMapper.toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class)))
                .thenReturn(paginatedResponseDto);


        PaginatedResponseDto response = roleService.search(searchBodyDto);

        assertEquals(response.getTotal(), paginatedResponseDto.getTotal());
        assertEquals(response.getPage(), paginatedResponseDto.getPage());
        assertEquals(response.getSize(), paginatedResponseDto.getSize());
        assertEquals(response.getData(), paginatedResponseDto.getData());

        verify(roleRepository).searchAll(pageable);
        verify(roleMapper).toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class));
    }


    @Test
    void findRole() {
        CreateRoleResponseDto createRoleResponseDto = getRoleUser(1L);
        Role role = createRole(1L);

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(createRoleResponseDto);

        CreateRoleResponseDto response = roleService.findRole(role.getId());

        assertEquals(role.getId(), response.getId());
        assertEquals(role.getName(), response.getName());
        assertEquals(role.getDescription(), response.getDescription());
        assertEquals(role.getIcon(), response.getIcon());
        assertEquals(role.isAdmin(), response.getIsAdmin());
        assertEquals(role.isDefaultRole(), response.getIsDefaultRole());
        assertEquals(role.isDeleted(), response.isDeleted());
        assertEquals(role.getDeletedAt(), response.getDeletedAt());

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(roleMapper).toResponse(role);

    }

    @Test
    void findRoleNotExists() {
        Role role = createRole(1L);

        when(roleRepository.findByIdAndDeletedFalse(role.getId()))
                .thenThrow(new RoleNotFoundException("The role does not exist"));

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.findRole(role.getId())
        );
        assertEquals("The role does not exist", exception.getMessage());
        verify(roleRepository).findByIdAndDeletedFalse(role.getId());

    }

    @Test
    void updateRole() {
        CreateRoleResponseDto createRoleResponseDto = getRoleAdmin(1L);
        Role role = createRole(1L);

        UpdateRoleDto updateRoleDto = UpdateRoleDto.builder()
                .name(createRoleResponseDto.getName())
                .description(createRoleResponseDto.getDescription())
                .icon(createRoleResponseDto.getIcon())
                .isAdmin(createRoleResponseDto.getIsAdmin())
                .isDefaultRole(createRoleResponseDto.getIsDefaultRole())
                .build();

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(createRoleResponseDto);

        CreateRoleResponseDto response = roleService.updateRole(role.getId(), updateRoleDto);

        assertEquals(response.getId(), createRoleResponseDto.getId());
        assertEquals(response.getName(), createRoleResponseDto.getName());
        assertEquals(response.getDescription(), createRoleResponseDto.getDescription());
        assertEquals(response.getIcon(), createRoleResponseDto.getIcon());
        assertEquals(response.getIsAdmin(), createRoleResponseDto.getIsAdmin());
        assertEquals(response.getIsDefaultRole(), createRoleResponseDto.getIsDefaultRole());
        assertEquals(response.isDeleted(), createRoleResponseDto.isDeleted());
        assertEquals(response.getDeletedAt(), createRoleResponseDto.getDeletedAt());

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(roleRepository).save(role);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void updateRoleNotExists() {
        Role role = createRole(1L);
        UpdateRoleDto updateRoleDto = UpdateRoleDto.builder()
                .name(role.getName())
                .description(role.getDescription())
                .icon(role.getIcon())
                .isAdmin(role.isAdmin())
                .isDefaultRole(role.isDefaultRole())
                .build();

        when(roleRepository.findByIdAndDeletedFalse(role.getId()))
                .thenThrow(new RoleNotFoundException("The role does not exist"));

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.updateRole(role.getId(), updateRoleDto)
        );
        assertEquals("The role does not exist", exception.getMessage());
        verify(roleRepository).findByIdAndDeletedFalse(role.getId());

    }

    @Test
    void deleteRole() {
        Role role = createRole(1L);
        DeleteResponseDto expectedResponse = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        DeleteResponseDto response = roleService.deleteRole(role.getId());

        assertEquals(expectedResponse.isAcknowledged(), response.isAcknowledged());
        assertEquals(expectedResponse.getDeletedCount(), response.getDeletedCount());

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(roleRepository).save(role);
    }

    @Test
    void deleteRoleNotExist() {
        Role role = createRole(1L);

        when(roleRepository.findByIdAndDeletedFalse(role.getId()))
                .thenThrow(new RoleNotFoundException("The role does not exist"));

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.deleteRole(role.getId())
        );
        assertEquals("The role does not exist", exception.getMessage());
        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
    }
}