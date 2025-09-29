package com.innowise.userservice.service;

import java.util.List;
/**
 * Generic interface for basic CRUD operations on a service layer.
 * Designed to be extended by domain-specific service interfaces.
 *
 * @param <T> the type of the domain object managed by the service
 */
public interface CrudService<T> {

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id the ID of the entity to retrieve
     * @return the entity with the given ID
     */
    T findById(Long id);

    /**
     * Retrieves all entities of type {@code T}.
     *
     * @return a list containing all entities
     */
    List<T> findAll();

    /**
     * Retrieves multiple entities by their identifiers.
     *
     * @param ids the list of IDs to retrieve
     * @return a list of entities matching the given IDs
     */
    List<T> findByIds(List<Long> ids);

    /**
     * Persists a new entity.
     *
     * @param obj the entity to create
     * @return the created entity with any generated fields populated
     */
    T create(T obj);

    /**
     * Updates an existing entity.
     *
     * @param obj the entity with updated fields
     */
    void update(T obj);

    /**
     * Deletes an entity by its unique identifier.
     *
     * @param id the ID of the entity to delete
     */
    void delete(Long id);
}