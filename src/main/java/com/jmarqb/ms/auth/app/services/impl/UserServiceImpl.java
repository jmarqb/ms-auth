package com.jmarqb.ms.auth.app.services.impl;

import com.jmarqb.ms.auth.app.dtos.request.*;
import com.jmarqb.ms.auth.app.dtos.response.*;
import com.jmarqb.ms.auth.app.entities.*;
import com.jmarqb.ms.auth.app.enums.Gender;
import com.jmarqb.ms.auth.app.exceptions.DuplicateKeyException;
import com.jmarqb.ms.auth.app.exceptions.RoleNotFoundException;
import com.jmarqb.ms.auth.app.exceptions.UserNotFoundException;
import com.jmarqb.ms.auth.app.repositories.*;
import com.jmarqb.ms.auth.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public CreateUserResponseDto save(CreateUserDto createUserDto) {
        Optional<Role> role = roleRepository.findByName("USER");

        if (role.isPresent()) {
            createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
            User newUser = userMapper.toEntity(createUserDto);
            if (newUser.getRoles() == null) {
                newUser.setRoles(new ArrayList<>());
            }
            newUser.getRoles().add(role.get());

            User savedUser = userRepository.save(newUser);
            CreateUserResponseDto response = userMapper.toResponse(savedUser);
            response.setRoles(savedUser.getRoles().stream().map(userMapper::map).toList());
            return response;

        } else {
            throw new RuntimeException("Default role ROLE_USER not found.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedResponseDto search(SearchBodyDto searchBodyDto) {
        try {
            List<User> users;
            String search = searchBodyDto.getSearch();

            Pageable pageable = PageRequest.of(searchBodyDto.getPage(), searchBodyDto.getSize(),
                    searchBodyDto.getSort().equalsIgnoreCase("asc") ?
                            Sort.Direction.ASC : Sort.Direction.DESC, "id");

            users = (search != null) ? userRepository.searchAllByRegex(search, pageable) : userRepository.searchAll(pageable);

            List<CreateUserResponseDto> response = new ArrayList<>();
            users.forEach(user -> response.add(userMapper.toResponse(user)));

            return userMapper.toPaginatedResponse(response, users.size(), searchBodyDto.getPage(), searchBodyDto.getSize(),
                    new Date());

        }catch (Exception e){
            throw new RuntimeException("Error searching users", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CreateUserResponseDto findUser(Long id) {
        User user = existsUser(id);
        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    public CreateUserResponseDto updateUser(Long id, UpdateUserDto updateUserDto) {
        User user = existsUser(id);
        updateUserFields(user, updateUserDto);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    @Override
    public DeleteResponseDto deleteUser(Long id) {
        User user = existsUser(id);
        user.setDeleted(true);
        user.setDeletedAt(new Date());
        userRepository.save(user);
        return DeleteResponseDto.builder().deletedCount(1).acknowledged(true).build();
    }

    @Transactional
    @Override
    public PaginatedResponseDto addRoleToManyUsers(RoleToUsersDto roleToUsersDto) {
        Role role = existsRole(roleToUsersDto.getRoleId());
        List<User> users = getExistingUsers(Arrays.asList(roleToUsersDto.getUsersId()));
        addRoleToList(users, role);
        return updateRoleInUsers(users);
    }
    @Override
    public PaginatedResponseDto removeRoleToManyUsers(RoleToUsersDto roleToUsersDto) {
        Role role = existsRole(roleToUsersDto.getRoleId());
        List<User> users = getExistingUsers(Arrays.asList(roleToUsersDto.getUsersId()));
        removeRoleToList(users, role);
        return updateRoleInUsers(users);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    private PaginatedResponseDto updateRoleInUsers(List<User> users) {
        try {
            userRepository.saveAll(users);

            List<CreateUserResponseDto> response = users.stream().map(userMapper::toResponse).toList();
            return userMapper.toPaginatedResponse(response, users.size(), 0, users.size(), new Date());

        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("The user already has this role");
        }
    }

    private Role existsRole(Long id) {
        Role role = roleRepository.findByIdAndDeletedFalse(id);
        if (role == null) {
            throw new RoleNotFoundException("The role does not exist");
        }
        return role;
    }

    private List<User> getExistingUsers(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            throw new UserNotFoundException("The users do not exist");
        }
        return users;
    }

    private void addRoleToList(List<User> users, Role role) {
        users.forEach(user -> {
            user.getRoles().add(role);
        });
    }

    private void removeRoleToList(List<User> users, Role role) {
        users.forEach(user -> {
            user.getRoles().remove(role);
        });
    }

    private User existsUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id);
        if (user == null) {
            throw new UserNotFoundException("The user does not exist");
        }
        return user;
    }

    private void updateUserFields(User user, UpdateUserDto updateUserDto) {
        if (updateUserDto.getFirstName() != null) user.setFirstName(updateUserDto.getFirstName());
        if (updateUserDto.getLastName() != null) user.setLastName(updateUserDto.getLastName());
        if (updateUserDto.getEmail() != null) user.setEmail(updateUserDto.getEmail());
        if (updateUserDto.getPhone() != null) user.setPhone(updateUserDto.getPhone());
        if (updateUserDto.getGender() != null) user.setGender(Gender.valueOf(updateUserDto.getGender()));
        if (updateUserDto.getCountry() != null) user.setCountry(updateUserDto.getCountry());

    }

}
