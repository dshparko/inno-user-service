package com.innowise.userservice.database.repository;

import com.innowise.userservice.database.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName UserRepository
 * @Description Repository interface for accessing and manipulating {@link User} entities.
 * Provides methods for CRUD operations.
 * @Author dshparko
 * @Date 08.09.2025 15:34
 * @Version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Persists the given {@link User} entity to the database.
     *
     * @param user the user entity to save
     * @return the saved {@link User} entity
     */
    User save(User user);

    /**
     * Retrieves a {@link User} entity by its ID, eagerly fetching associated {@code userCards}.
     * <p>
     * Uses JPQL with {@code LEFT JOIN FETCH} to ensure that the user's cards are loaded in the same query,
     * avoiding the N+1 select problem and enabling immediate access to the card list.
     * </p>
     *
     * @param id the unique identifier of the user
     * @return an {@link Optional} containing the {@link User} with initialized {@code userCards}, or empty if not found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userCards WHERE u.id = :id")
    Optional<User> findByIdWithCards(@Param("id") Long id);

    /**
     * Retrieves all {@link User} entities matching the given list of IDs.
     *
     * @param ids a list of user IDs
     * @return a list of {@link User} entities with matching IDs, or an empty list if none found
     */
    List<User> findByIdIn(List<Long> ids);

    /**
     * Retrieves a {@link User} entity by its email address.
     * <p>
     * Uses JPQL to locate the user with the specified email.
     * </p>
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the {@link User} entity if found, or empty if no match exists
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Updates the fields of a user with the specified ID.
     * <p>
     * This method modifies the {@code name}, {@code surname}, {@code email}, and {@code birthDate} fields.
     * It is executed as a JPQL update query and does not load the entity into persistence context.
     * </p>
     *
     * @param id        the ID of the user to update
     * @param name      the new first name of the user
     * @param surname   the new surname of the user
     * @param email     the new email address of the user
     * @param birthDate the new birthdate of the user
     * @return the number of affected rows (should be 0 or 1)
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u " +
            "SET u.name = :name, u.surname = :surname, u.email = :email, " +
            "u.birthDate = :birthDate " +
            "WHERE u.id = :id")
    int updateUserById(@Param("id") Long id,
                       @Param("name") String name,
                       @Param("surname") String surname,
                       @Param("email") String email,
                       @Param("birthDate") LocalDate birthDate);

    /**
     * Deletes the user with the specified ID from the database.
     *
     * @param id the ID of the user to delete
     */
    void deleteById(Long id);

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all {@link User} entities
     */
    List<User> findAll();

    /**
     * Retrieves a paginated list of {@link User} entities that match the given {@link Specification}.
     * <p>
     * This method supports dynamic filtering using JPA Criteria API and returns results
     * according to the provided {@link Pageable} configuration.
     * </p>
     *
     * @param spec     the specification defining filtering criteria; can be {@code null} for no filtering
     * @param pageable the pagination and sorting information
     * @return a {@link Page} of {@link User} entities matching the specification
     */
    Page<User> findAll(Specification<User> spec, Pageable pageable);

}
