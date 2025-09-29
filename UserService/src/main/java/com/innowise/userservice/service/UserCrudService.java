package com.innowise.userservice.service;
/**
 * Extension of {@link CrudService} for user-specific operations.
 * Adds domain-specific lookup capabilities beyond basic CRUD.
 *
 * @param <T> the type of the user DTO or entity managed by the service
 */
public interface UserCrudService<T> extends CrudService<T> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the user associated with the given email
     */
    T findByEmail(String email);
}
