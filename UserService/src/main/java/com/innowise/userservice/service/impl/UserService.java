package com.innowise.userservice.service.impl;

import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.database.repository.UserRepository;
import com.innowise.userservice.database.specification.UserSpecification;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.dto.UserFilterDto;
import com.innowise.userservice.http.exception.ModificationException;
import com.innowise.userservice.http.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @ClassName UserService
 * @Description Service layer for managing user operations.
 * Handles creation, retrieval, update, and deletion of users.
 * @Author dshparko
 * @Date 11.09.2025 7:48
 * @Version 1.0
 */

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService implements UserCrudService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Creates a new user based on the provided request DTO.
     *
     * @param request DTO containing user creation data
     * @return mapped {@link UserDto} representing the saved user
     */
    @Transactional
    @Override
    public UserDto create(UserDto request) {
        User user = userMapper.mapToEntity(request);
        return userMapper.mapToDto(userRepository.save(user));
    }

    /**
     * Retrieves a user by ID and caches the result.
     *
     * @param id unique identifier of the user
     * @return mapped {@link UserDto} if found
     * @throws ResourceNotFoundException if no user exists with the given ID
     */

    @Override
    @Cacheable(value = "userWithCards", key = "#id")
    public UserDto findById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findByIdWithCards(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.mapToDto(user);
    }

    /**
     * Retrieves multiple users by their IDs.
     *
     * @param ids list of user IDs to fetch
     * @return list of mapped {@link UserDto} objects
     */
    @Override
    public List<UserDto> findByIds(List<Long> ids) {
        return userMapper.mapToDtoList(userRepository.findByIdIn(ids));
    }

    /**
     * Retrieves a user by email.
     *
     * @param email email address of the user
     * @return mapped {@link UserDto} if found
     * @throws ResourceNotFoundException if no user exists with the given email
     */
    @Cacheable(value = "userByEmail", key = "#email")
    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return userMapper.mapToDto(user);
    }

    /**
     * Updates an existing user based on the provided request DTO.
     * Evicts cached entry to ensure consistency.
     *
     * @param request DTO containing updated user data
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    @Caching(evict = {
            @CacheEvict(value = "userWithCards", key = "#request.id()"),
            @CacheEvict(value = "userByEmail", key = "#request.email()")
    })
    @Transactional
    @Override
    public void update(UserDto request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getId()));

        int updated = userRepository.updateUserById(
                user.getId(),
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                request.getBirthDate()
        );

        if (updated == 0) {
            throw new ModificationException("User", request.getId());
        }
    }

    /**
     * Deletes a user by ID.
     * Evicts cached entry to ensure consistency.
     *
     * @param id unique identifier of the user to delete
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    @CacheEvict(value = "userWithCards", key = "#id")
    @Transactional
    @Override
    public void delete(Long id) {
        User user = userRepository.findByIdWithCards(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        userRepository.delete(user);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return list of mapped {@link UserDto} objects
     */
    @Override
    public List<UserDto> findAll() {
        return userMapper.mapToDtoList(userRepository.findAll());
    }


    public Page<UserDto> findAll(UserFilterDto filter, Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(UserSpecification.from(filter), pageable);

        List<UserDto> dtoList = userMapper.mapToDtoList(usersPage.getContent());

        return new PageImpl<>(dtoList, pageable, usersPage.getTotalElements());
    }
}
