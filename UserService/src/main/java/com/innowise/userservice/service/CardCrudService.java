package com.innowise.userservice.service;

import java.util.List;

/**
 * Extension of {@link CrudService} for card-specific operations.
 * Adds domain-specific lookup capabilities for cards associated with users.
 *
 * @param <T> the type of the card DTO or entity managed by the service
 */
public interface CardCrudService<T> extends CrudService<T> {

    /**
     * Retrieves all cards associated with a specific user.
     *
     * @param id the ID of the user whose cards should be retrieved
     * @return a list of cards linked to the given user ID
     */
    List<T> findByUserId(Long id);
}
