package com.innowise.userservice.http.controller;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.dto.UserFilterDto;
import com.innowise.userservice.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName UserController
 * @Description REST controller for managing {@link com.innowise.userservice.database.entity.User} entities and their associated {@link com.innowise.userservice.dto.CardDto} data.
 * Provides endpoints for user CRUD operations and card retrieval.
 * @Author dshparko
 * @Date 11.09.2025 8:52
 * @Version 1.0
 */

@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Retrieves users based on optional filters and pagination parameters.
     *
     * @param filter   optional filter criteria (email, id, ids)
     * @param pageable pagination and sorting configuration
     * @return paginated list of {@link UserDto} objects; 204 No Content if none found
     */
    @GetMapping
    public ResponseEntity<Page<UserDto>> search(UserFilterDto filter, Pageable pageable) {
        Page<UserDto> page;

        if (filter.id() != null) {
            UserDto user = userService.findById(filter.id());
            List<UserDto> list = user != null ? List.of(user) : List.of();
            page = new PageImpl<>(list, pageable, list.size());
        } else if (filter.email() != null && !filter.email().isBlank()) {
            UserDto user = userService.findByEmail(filter.email());
            List<UserDto> list = user != null ? List.of(user) : List.of();
            page = new PageImpl<>(list, pageable, list.size());
        } else {
            page = userService.findAll(filter, pageable);
        }

        return page.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(page);
    }

    /**
     * Creates a new user.
     *
     * @param user DTO containing user creation data
     * @return {@link UserDto} representing the newly created user
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto user) {
        return ResponseEntity.ok(userService.create(user));
    }

    /**
     * Updates an existing user.
     *
     * @param user DTO containing updated user data
     * @return 200 OK if update was successful
     */
    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserDto user) {
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return 200 OK if deletion was successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
