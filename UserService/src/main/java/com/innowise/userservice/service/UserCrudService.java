package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.dto.UserFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Extension of {@link CrudService} for user-specific operations.
 * Adds domain-specific lookup capabilities beyond basic CRUD.
 *
 */
public interface UserCrudService extends CrudService<UserDto> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the user associated with the given email
     */
    UserDto findByEmail(String email);

    Page<UserDto> findAll(UserFilterDto filterDto, Pageable pageable);

}
