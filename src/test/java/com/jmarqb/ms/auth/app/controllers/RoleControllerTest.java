package com.jmarqb.ms.auth.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.auth.app.dtos.request.*;
import com.jmarqb.ms.auth.app.dtos.response.*;
import com.jmarqb.ms.auth.app.exceptions.RoleNotFoundException;
import com.jmarqb.ms.auth.app.security.config.SpringSecurityConfig;
import com.jmarqb.ms.auth.app.services.RoleService;
import com.jmarqb.ms.auth.app.services.impl.JpaUserDetailsService;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.jmarqb.ms.auth.app.data.Data.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(value = RoleController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(username = "admin@email.com", authorities = "ADMIN")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JpaUserDetailsService jpaUserDetailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "admin@email.com", authorities = "ADMIN")
    void create() throws Exception {
        CreateRoleDto createRoleDto = createRoleAdmin();
        CreateRoleResponseDto createRoleResponseDto = getRoleAdmin(1L);

        when(roleService.save(createRoleDto)).thenReturn(createRoleResponseDto);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoleDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createRoleResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(createRoleResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(createRoleResponseDto.getDescription()))
                .andExpect(jsonPath("$.icon").value(createRoleResponseDto.getIcon()))
                .andExpect(jsonPath("$.isAdmin").value(createRoleResponseDto.getIsAdmin()))
                .andExpect(jsonPath("$.isDefaultRole").value(createRoleResponseDto.getIsDefaultRole()))
                .andExpect(jsonPath("$.deleted").value(createRoleResponseDto.isDeleted()))
                .andExpect(jsonPath("$.deletedAt").value(createRoleResponseDto.getDeletedAt()));

        verify(roleService).save(createRoleDto);
    }

    @Test
    @WithMockUser(username = "admin@email.com", authorities = "ADMIN")
    void createThrowsHttpMessageNotReadableException() throws Exception {
        CreateRoleDto createRoleDto = createRoleAdmin();
        CreateRoleResponseDto createRoleResponseDto = getRoleAdmin(1L);

        when(roleService.save(createRoleDto)).thenThrow(new HttpMessageNotReadableException("Cannot deserialize value for Json"));

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Json Error"))
                .andExpect(jsonPath("$.message").value("Cannot deserialize value for Json"));

    }

    @Test
    void search() throws Exception {
        SearchBodyDto searchBodyDto = SearchBodyDto.builder()
                .search("")
                .page(0)
                .size(10)
                .sort("ASC")
                .build();

        List<CreateRoleResponseDto> list = List.of(getRoleAdmin(1L), getRoleUser(2L));
        PaginatedResponseDto paginatedResponseDto = PaginatedResponseDto.builder()
                .total(2)
                .page(0)
                .size(10)
                .data(list)
                .build();

        when(roleService.search(searchBodyDto)).thenReturn(paginatedResponseDto);

        mockMvc.perform(post("/api/roles/search").contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(searchBodyDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(paginatedResponseDto.getTotal()))
                .andExpect(jsonPath("$.page").value(paginatedResponseDto.getPage()))
                .andExpect(jsonPath("$.size").value(paginatedResponseDto.getSize()))
                .andExpect(jsonPath("$.data[0]").value(paginatedResponseDto.getData().get(0)))
                .andExpect(jsonPath("$.data[1]").value(paginatedResponseDto.getData().get(1)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(roleService).search(searchBodyDto);
    }

    @Test
    void findRole() throws Exception {
        CreateRoleResponseDto admin = getRoleAdmin(1L);
        when(roleService.findRole(1L)).thenReturn(admin);

        mockMvc.perform(get("/api/roles/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(admin.getId()))
                .andExpect(jsonPath("$.name").value(admin.getName()))
                .andExpect(jsonPath("$.description").value(admin.getDescription()))
                .andExpect(jsonPath("$.icon").value(admin.getIcon()))
                .andExpect(jsonPath("$.isAdmin").value(admin.getIsAdmin()))
                .andExpect(jsonPath("$.isDefaultRole").value(admin.getIsDefaultRole()))
                .andExpect(jsonPath("$.deleted").value(admin.isDeleted()))
                .andExpect(jsonPath("$.deletedAt").value(admin.getDeletedAt()));

        verify(roleService).findRole(1L);
    }

    @Test
    void findRoleIfNotExists() throws Exception {
        when(roleService.findRole(1L)).thenThrow(new RoleNotFoundException("The role does not exist"));

        mockMvc.perform(get("/api/roles/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The role does not exist"));

        verify(roleService).findRole(1L);
    }

    @Test
    void updateRole() throws Exception {
        CreateRoleResponseDto role = getRoleUser(2L);
        UpdateRoleDto updateRoleDto = UpdateRoleDto.builder()
                .name("ADMIN")
                .description("Admin Description")
                .icon("icon")
                .isAdmin(true)
                .isDefaultRole(false)
                .build();

        role.setName(updateRoleDto.getName());
        role.setDescription(updateRoleDto.getDescription());
        role.setIcon(updateRoleDto.getIcon());
        role.setIsAdmin(updateRoleDto.getIsAdmin());
        role.setIsDefaultRole(updateRoleDto.getIsDefaultRole());

        when(roleService.updateRole(role.getId(), updateRoleDto)).thenReturn(getUserUpdatedToAdmin(role));

        mockMvc.perform(patch("/api/roles/{id}", role.getId())
                        .contentType("application/json;charset=UTF-8").content(objectMapper.writeValueAsString(updateRoleDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.name").value(role.getName()))
                .andExpect(jsonPath("$.description").value(role.getDescription()))
                .andExpect(jsonPath("$.icon").value(role.getIcon()))
                .andExpect(jsonPath("$.isAdmin").value(role.getIsAdmin()))
                .andExpect(jsonPath("$.isDefaultRole").value(role.getIsDefaultRole()))
                .andExpect(jsonPath("$.deleted").value(role.isDeleted()))
                .andExpect(jsonPath("$.deletedAt").value(role.getDeletedAt()));

        verify(roleService).updateRole(role.getId(), updateRoleDto);

    }

    @Test
    void updateRoleWhenRoleNotExists() throws Exception {
        UpdateRoleDto updateRoleDto = UpdateRoleDto.builder()
                .name("ADMIN")
                .description("Admin Description")
                .icon("icon")
                .isAdmin(true)
                .isDefaultRole(false)
                .build();

        when(roleService.updateRole(1L, updateRoleDto)).thenThrow(new RoleNotFoundException("The role does not exist"));

        mockMvc.perform(patch("/api/roles/{id}", 1L)
                        .contentType("application/json;charset=UTF-8").content(objectMapper.writeValueAsString(updateRoleDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The role does not exist"));

        verify(roleService).updateRole(1L, updateRoleDto);

    }

    @Test
    void removeRole() throws Exception {
        DeleteResponseDto response = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();
        when(roleService.deleteRole(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/roles/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(response.getDeletedCount()))
                .andExpect(jsonPath("$.acknowledged").value(response.isAcknowledged()));

        verify(roleService).deleteRole(1L);
    }

    @Test
    void removeRoleWhenRoleNotExists() throws Exception {
        when(roleService.deleteRole(1L)).thenThrow(new RoleNotFoundException("The role does not exist"));

        mockMvc.perform(delete("/api/roles/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("The role does not exist"));

        verify(roleService).deleteRole(1L);
    }


}