package com.innowise.userservice.http.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

public class ModificationException extends RuntimeException {
    @Getter
    @Serial
    private final UUID errorId;

    public ModificationException(String type, Object identifier) {
        super(type + " update failed for id: " + identifier);
        this.errorId = UUID.randomUUID();
    }
}
