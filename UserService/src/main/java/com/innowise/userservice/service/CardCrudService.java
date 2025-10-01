package com.innowise.userservice.service;

import com.innowise.userservice.dto.CardDto;

import java.util.List;

/**
 * Extension of {@link CrudService} for card-specific operations.
 * Adds domain-specific lookup capabilities for cards associated with users.
 *
 */
public interface CardCrudService extends CrudService<CardDto> {

    /**
     * Retrieves all cards associated with a specific user.
     *
     * @param id the ID of the user whose cards should be retrieved
     * @return a list of cards linked to the given user ID
     */
    List<CardDto> findByUserId(Long id);
}
