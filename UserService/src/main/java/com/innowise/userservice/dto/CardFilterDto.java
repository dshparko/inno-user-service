package com.innowise.userservice.dto;

import java.util.List;

public record CardFilterDto(
        Long id,
        Long userId,
        List<Long> ids
) {
}
