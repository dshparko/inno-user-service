package com.innowise.userservice.http.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

/**
 * @ClassName UserNotFoundException
 * @Description Custom runtime exception thrown when a {@link com.innowise.userservice.database.entity.User} is not found.
 * @Author dshparko
 * @Date 11.09.2025 15:50
 * @Version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    @Getter
    @Serial
    private final UUID errorId = UUID.randomUUID();

    public ResourceNotFoundException(String type, Object identifier) {
        super(type + " with identifier " + identifier + " wasn't found");
    }
}

