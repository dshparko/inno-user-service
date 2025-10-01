package com.innowise.userservice.dto;

import java.util.List;

public record UserFilterDto(
        Long id,
        String email,
        List<Long> ids
) {
}
