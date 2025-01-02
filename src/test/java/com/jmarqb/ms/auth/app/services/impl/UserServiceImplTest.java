package com.jmarqb.ms.auth.app.services.impl;

import com.jmarqb.ms.auth.app.dtos.request.CreateUserDto;
import com.jmarqb.ms.auth.app.dtos.request.RoleToUsersDto;
import com.jmarqb.ms.auth.app.dtos.request.SearchBodyDto;
import com.jmarqb.ms.auth.app.dtos.request.UpdateUserDto;
import com.jmarqb.ms.auth.app.dtos.response.CreateUserResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.DeleteResponseDto;
import com.jmarqb.ms.auth.app.dtos.response.PaginatedResponseDto;
import com.jmarqb.ms.auth.app.entities.Role;
import com.jmarqb.ms.auth.app.entities.User;
import com.jmarqb.ms.auth.app.entities.UserMapper;
import com.jmarqb.ms.auth.app.exceptions.DuplicateKeyException;
import com.jmarqb.ms.auth.app.exceptions.RoleNotFoundException;
import com.jmarqb.ms.auth.app.exceptions.UserNotFoundException;
import com.jmarqb.ms.auth.app.repositories.RoleRepository;
import com.jmarqb.ms.auth.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.jmarqb.ms.auth.app.data.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userMapper);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "roleRepository", roleRepository);
        ReflectionTestUtils.setField(userService,"passwordEncoder", passwordEncoder);
    }

    @Test
    void save() {
        User user = createUser(1L);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = createRole(user.getId());

        CreateUserDto createUserDto = createUserDto();
        createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        CreateUserResponseDto createUserResponseDto = createAdminUserResponseDto(user.getId());
        createUserResponseDto.setRoles(user.getRoles().stream().map(userMapper::map).toList());

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(userMapper.toEntity(createUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(createUserResponseDto);

        CreateUserResponseDto response = userService.save(createUserDto);

        assertEquals(createUserResponseDto.getId(), response.getId());
        assertEquals(createUserResponseDto.getFirstName(), response.getFirstName());
        assertEquals(createUserResponseDto.getLastName(), response.getLastName());
        assertEquals(createUserResponseDto.getEmail(), response.getEmail());
        assertEquals(createUserResponseDto.getPhone(), response.getPhone());
        assertEquals(createUserResponseDto.getRoles(), response.getRoles());

        verify(roleRepository).findByName("USER");
        verify(userMapper).toEntity(createUserDto);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }
    @Test
    void save_shouldThrowRuntimeException_whenRoleNotFound() {
        CreateUserDto createUserDto = createUserDto();

        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.save(createUserDto)
        );

        assertEquals("Default role ROLE_USER not found.", exception.getMessage());

        verify(roleRepository).findByName("USER");

        verifyNoInteractions(userMapper);
        verifyNoInteractions(userRepository);
    }
    @Test
    void searchContainsRegex() {
        SearchBodyDto searchBodyDto = createSearchBodyDto("a", 0, 10, "ASC");

        List<User> list = List.of(createUser(1L), createUser(2L));
        List<CreateUserResponseDto> list1 = List.of(createAdminUserResponseDto(1L), createAdminUserResponseDto(2L));

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

        when(userRepository.searchAllByRegex(searchBodyDto.getSearch(), pageable)).thenReturn(list);
        when(userMapper.toResponse(list.get(0))).thenReturn(list1.get(0));
        when(userMapper.toResponse(list.get(1))).thenReturn(list1.get(1));
        when(userMapper.toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class)))
                .thenReturn(paginatedResponseDto);


        PaginatedResponseDto response = userService.search(searchBodyDto);

        assertEquals(paginatedResponseDto.getTotal(), response.getTotal());
        assertEquals(paginatedResponseDto.getPage(), response.getPage());
        assertEquals(paginatedResponseDto.getSize(), response.getSize());
        assertEquals(paginatedResponseDto.getData(), response.getData());

        verify(userRepository).searchAllByRegex(searchBodyDto.getSearch(), pageable);
        verify(userMapper).toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class));
    }

    @Test
    void searchNotRegex() {
        SearchBodyDto searchBodyDto = createSearchBodyDto(null, 0, 10, "ASC");

        List<User> list = List.of(createUser(1L), createUser(2L));
        List<CreateUserResponseDto> list1 = List.of(createAdminUserResponseDto(1L), createAdminUserResponseDto(2L));

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

        when(userRepository.searchAll( pageable)).thenReturn(list);
        when(userMapper.toResponse(list.get(0))).thenReturn(list1.get(0));
        when(userMapper.toResponse(list.get(1))).thenReturn(list1.get(1));
        when(userMapper.toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class)))
                .thenReturn(paginatedResponseDto);


        PaginatedResponseDto response = userService.search(searchBodyDto);

        assertEquals(paginatedResponseDto.getTotal(), response.getTotal());
        assertEquals(paginatedResponseDto.getPage(), response.getPage());
        assertEquals(paginatedResponseDto.getSize(), response.getSize());
        assertEquals(paginatedResponseDto.getData(), response.getData());

        verify(userRepository).searchAll(pageable);
        verify(userMapper).toPaginatedResponse(eq(list1), eq(list.size()), eq(searchBodyDto.getPage()), eq(searchBodyDto.getSize()), any(Date.class));

    }
    @Test
    void search_shouldThrowRuntimeException() {
        SearchBodyDto searchBodyDto = createSearchBodyDto("a", 0, 10, "ASC");

        Pageable pageable = PageRequest.of(searchBodyDto.getPage(),
                searchBodyDto.getSize(),
                searchBodyDto.getSort().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, "id");

        when(userRepository.searchAllByRegex(searchBodyDto.getSearch(), pageable))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.search(searchBodyDto)
        );

        assertEquals("Error searching users", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());

        verify(userRepository).searchAllByRegex(searchBodyDto.getSearch(), pageable);
    }

    @Test
    void findUser() {
        CreateUserResponseDto createUserResponseDto = createAdminUserResponseDto(1L);
        User user = createUser(1L);
        when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(createUserResponseDto);

        CreateUserResponseDto response = userService.findUser(user.getId());

        assertEquals(createUserResponseDto.getId(), response.getId());
        assertEquals(createUserResponseDto.getFirstName(), response.getFirstName());
        assertEquals(createUserResponseDto.getLastName(), response.getLastName());
        assertEquals(createUserResponseDto.getEmail(), response.getEmail());
        assertEquals(createUserResponseDto.getPhone(), response.getPhone());
        assertEquals(createUserResponseDto.getGender(), response.getGender());
        assertEquals(createUserResponseDto.getCountry(), response.getCountry());
        assertEquals(createUserResponseDto.getDeletedAt(), response.getDeletedAt());
        assertEquals(createUserResponseDto.isDeleted(), response.isDeleted());
        assertEquals(createUserResponseDto.getRoles(), response.getRoles());

        verify(userRepository).findByIdAndDeletedFalse(user.getId());
        verify(userMapper).toResponse(user);
    }

    @Test
    void findUserNotFound() {
        User user = createUser(1L);

        when(userRepository.findByIdAndDeletedFalse(user.getId())).
                thenThrow(new UserNotFoundException("The user does not exist"));

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUser(user.getId())
        );
        assertEquals("The user does not exist", exception.getMessage());
        verify(userRepository).findByIdAndDeletedFalse(user.getId());
    }

    @Test
    void updateUser() {
        CreateUserResponseDto createUserResponseDto = createAdminUserResponseDto(1L);
        User user = createUser(1L);
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender().name())
                .country(user.getCountry())
                .build();

        when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(createUserResponseDto);

        CreateUserResponseDto response = userService.updateUser(user.getId(), updateUserDto);

        assertEquals(createUserResponseDto.getId(), response.getId());
        assertEquals(createUserResponseDto.getFirstName(), response.getFirstName());
        assertEquals(createUserResponseDto.getLastName(), response.getLastName());
        assertEquals(createUserResponseDto.getEmail(), response.getEmail());
        assertEquals(createUserResponseDto.getPhone(), response.getPhone());
        assertEquals(createUserResponseDto.getGender(), response.getGender());
        assertEquals(createUserResponseDto.getCountry(), response.getCountry());
        assertEquals(createUserResponseDto.getDeletedAt(), response.getDeletedAt());
        assertEquals(createUserResponseDto.isDeleted(), response.isDeleted());
        assertEquals(createUserResponseDto.getRoles(), response.getRoles());

        verify(userRepository).findByIdAndDeletedFalse(user.getId());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUserNotFound(){
        User user = createUser(1L);
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender().name())
                .country(user.getCountry())
                .build();

        when(userRepository.findByIdAndDeletedFalse(user.getId())).thenThrow(new UserNotFoundException("The user does not exist"));

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(user.getId(), updateUserDto)
        );
        assertEquals("The user does not exist", exception.getMessage());
        verify(userRepository).findByIdAndDeletedFalse(user.getId());
    }

    @Test
    void deleteUser() {
        User user = createUser(1L);
        DeleteResponseDto expectedResponse = DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();


        when(userRepository.findByIdAndDeletedFalse(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        DeleteResponseDto response = userService.deleteUser(user.getId());

        assertEquals(expectedResponse.isAcknowledged(),response.isAcknowledged());
        assertEquals(expectedResponse.getDeletedCount(),response.getDeletedCount());

        verify(userRepository).findByIdAndDeletedFalse(user.getId());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserNotFound(){
        User user = createUser(1L);

        when(userRepository.findByIdAndDeletedFalse(user.getId())).thenThrow(new UserNotFoundException("The user does not exist"));
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(user.getId())
        );
        assertEquals("The user does not exist", exception.getMessage());
        verify(userRepository).findByIdAndDeletedFalse(user.getId());

    }

    @Test
    void addRoleToManyUsers() {
        Role role = createRole(1L);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        List<User> users = List.of(user1, user2);

        Long[] usersIds = {user1.getId(), user2.getId()};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(usersIds, role.getId());

        CreateUserResponseDto responseDto1 = createAdminUserResponseDto(user1.getId());
        CreateUserResponseDto responseDto2 = createAdminUserResponseDto(user2.getId());

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(userRepository.findAllById(Arrays.asList(usersIds[0], usersIds[1]))).thenReturn(users);
        when(userMapper.toResponse(user1)).thenReturn(responseDto1);
        when(userMapper.toResponse(user2)).thenReturn(responseDto2);
        when(userMapper.toPaginatedResponse(anyList(), anyInt(), anyInt(), anyInt(), any(Date.class)))
                .thenReturn(PaginatedResponseDto.builder().data(List.of(responseDto1, responseDto2)).build());

        PaginatedResponseDto response = userService.addRoleToManyUsers(roleToUsersDto);

        assertNotNull(response);
        assertEquals(2, response.getData().size());
        assertTrue(user1.getRoles().contains(role));
        assertTrue(user2.getRoles().contains(role));

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(userRepository).findAllById(Arrays.asList(usersIds[0], usersIds[1]));
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
        verify(userMapper).toPaginatedResponse(anyList(), anyInt(), anyInt(), anyInt(), any(Date.class));

    }

    @Test
    void addRoleToManyUsers_shouldThrowRoleNotFoundException() {
        Long roleId = 1L;
        Long[] userIds = {1L, 2L};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, roleId);

        when(roleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(null);

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> userService.addRoleToManyUsers(roleToUsersDto)
        );

        assertEquals("The role does not exist", exception.getMessage());

        verify(roleRepository).findByIdAndDeletedFalse(roleId);
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void addRoleToManyUsers_shouldThrowUserNotFoundException_whenUsersDoNotExist() {
        Long roleId = 1L;
        Role role = createRole(roleId);
        Long[] userIds = {1L, 2L};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, roleId);

        when(roleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(role);
        when(userRepository.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(Collections.emptyList());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.addRoleToManyUsers(roleToUsersDto)
        );

        assertEquals("The users do not exist", exception.getMessage());

        verify(roleRepository).findByIdAndDeletedFalse(roleId);
        verify(userRepository).findAllById(Arrays.asList(userIds[0], userIds[1]));
        verifyNoInteractions(userMapper);
    }

    @Test
    void addRoleToManyUsers_shouldThrowDuplicateKeyException() {
        Role role = createRole(1L);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        user1.getRoles().add(role);
        List<User> users = List.of(user1, user2);

        Long[] userIds = {user1.getId(), user2.getId()};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, role.getId());

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(userRepository.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(users);
        doThrow(new DuplicateKeyException("The user already has this role"))
                .when(userRepository).saveAll(users);

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
                () -> userService.addRoleToManyUsers(roleToUsersDto)
        );

        assertEquals("The user already has this role", exception.getMessage());

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(userRepository).findAllById(Arrays.asList(userIds[0], userIds[1]));
        verify(userRepository).saveAll(users);
        verifyNoInteractions(userMapper);
    }

    @Test
    void removeRoleToManyUsers_shouldThrowRoleNotFoundException() {
        Long roleId = 1L;
        Long[] userIds = {1L, 2L};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, roleId);

        when(roleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(null);

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> userService.removeRoleToManyUsers(roleToUsersDto)
        );

        assertEquals("The role does not exist", exception.getMessage());

        verify(roleRepository).findByIdAndDeletedFalse(roleId);
        verifyNoInteractions(userRepository);
    }

    @Test
    void removeRoleToManyUsers_shouldThrowUserNotFoundException() {
        Long roleId = 1L;
        Role role = createRole(roleId);
        Long[] userIds = {1L, 2L};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, roleId);

        when(roleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(role);
        when(userRepository.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(Collections.emptyList());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.removeRoleToManyUsers(roleToUsersDto)
        );

        assertEquals("The users do not exist", exception.getMessage());

        verify(roleRepository).findByIdAndDeletedFalse(roleId);
        verify(userRepository).findAllById(Arrays.asList(userIds[0], userIds[1]));
    }

    @Test
    void removeRoleFromUsersEvenIfSomeDoNotHaveIt() {
        Role role = createRole(1L);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        user1.getRoles().add(role); // Only user1 has this role
        List<User> users = List.of(user1, user2);

        Long[] userIds = {user1.getId(), user2.getId()};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, role.getId());

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(userRepository.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(users);
        when(userMapper.toResponse(user1)).thenReturn(createAdminUserResponseDto(user1.getId()));
        when(userMapper.toResponse(user2)).thenReturn(createAdminUserResponseDto(user2.getId()));
        when(userMapper.toPaginatedResponse(anyList(), anyInt(), anyInt(), anyInt(), any(Date.class))).thenReturn(PaginatedResponseDto.builder().build());

        PaginatedResponseDto response = userService.removeRoleToManyUsers(roleToUsersDto);

        assertEquals(PaginatedResponseDto.builder().build(), response);

        assertTrue(user1.getRoles().contains(role));
        assertFalse(user2.getRoles().contains(role));

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(userRepository).findAllById(Arrays.asList(userIds[0], userIds[1]));
        verify(userRepository).saveAll(users);
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
    }
    @Test
    void HandleUnexpectedError_whenSavingUsers() {
        Role role = createRole(1L);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        user1.getRoles().add(role);
        user2.getRoles().add(role);
        List<User> users = List.of(user1, user2);

        Long[] userIds = {user1.getId(), user2.getId()};
        RoleToUsersDto roleToUsersDto = new RoleToUsersDto(userIds, role.getId());

        when(roleRepository.findByIdAndDeletedFalse(role.getId())).thenReturn(role);
        when(userRepository.findAllById(Arrays.asList(userIds[0], userIds[1]))).thenReturn(users);
        doThrow(new RuntimeException("Unexpected error"))
                .when(userRepository).saveAll(users);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.removeRoleToManyUsers(roleToUsersDto)
        );

        assertEquals("Unexpected error", exception.getMessage());

        verify(roleRepository).findByIdAndDeletedFalse(role.getId());
        verify(userRepository).findAllById(Arrays.asList(userIds[0], userIds[1]));
        verify(userRepository).saveAll(users);
    }

    @Test
    void existsByEmail_shouldReturnTrue() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_shouldReturnFalse() {
        String email = "nonexistent@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }
    @Test
    void existsByPhone_shouldReturnTrue() {
        String phone = "123456789";

        when(userRepository.existsByPhone(phone)).thenReturn(true);

        boolean result = userService.existsByPhone(phone);

        assertTrue(result);
        verify(userRepository).existsByPhone(phone);
    }

    @Test
    void existsByPhone_shouldReturnFalse() {
        String phone = "987654321";

        when(userRepository.existsByPhone(phone)).thenReturn(false);

        boolean result = userService.existsByPhone(phone);

        assertFalse(result);
        verify(userRepository).existsByPhone(phone);
    }
}