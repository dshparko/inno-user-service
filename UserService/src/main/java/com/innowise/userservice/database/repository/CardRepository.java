package com.innowise.userservice.database.repository;

import com.innowise.userservice.database.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and manipulating {@link Card} entities in the database.
 * <p>
 * Provides standard CRUD operations via {@link JpaRepository}, as well as advanced filtering
 * through {@link JpaSpecificationExecutor}. Includes custom JPQL queries for card updates,
 * retrieval by user or card identifiers, and paginated search with specifications.
 * </p>
 *
 * @author dshparko
 * @version 1.0
 * @since 08.09.2025
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    /**
     * Persists the given {@link Card} entity to the database.
     *
     * @param card the card entity to save
     * @return the saved {@link Card} entity
     */
    Card save(Card card);

    /**
     * Retrieves a {@link Card} entity by its unique identifier.
     *
     * @param id the ID of the card
     * @return an {@link Optional} containing the card if found, or empty if not
     */
    Optional<Card> findById(Long id);

    /**
     * Checks whether a card with the given ID exists in the database.
     *
     * @param id the ID of the card
     * @return {@code true} if a card with the given ID exists, {@code false} otherwise
     */
    boolean existsById(Long id);

    /**
     * Retrieves all cards associated with a specific user.
     * <p>
     * Uses {@code JOIN FETCH} to eagerly load the associated {@link com.innowise.userservice.database.entity.User} entity.
     * </p>
     *
     * @param userId the ID of the user
     * @return a list of {@link Card} entities linked to the specified user
     */
    @Query("SELECT c FROM Card c JOIN FETCH c.user WHERE c.user.id = :userId")
    List<Card> findCardsByUserId(@Param("userId") Long userId);

    /**
     * Retrieves all cards matching the given list of IDs.
     *
     * @param ids a list of card IDs
     * @return a list of {@link Card} entities with matching IDs, or an empty list if none found
     */
    @Query("SELECT c FROM Card c WHERE c.id IN :ids")
    List<Card> findCardsByIdIn(List<Long> ids);

    /**
     * Updates the fields of a card with the specified ID.
     * <p>
     * This method modifies the {@code user}, {@code number}, {@code holder}, and {@code expirationDate} fields.
     * </p>
     *
     * @param id             the ID of the card to update
     * @param userId         the new user ID to associate with the card
     * @param number         the new card number
     * @param holder         the new cardholder name
     * @param expirationDate the new expiration date
     * @return the number of affected rows (should be 0 or 1)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.user.id = :userId, c.number = :number, c.holder = :holder, c.expirationDate = :expirationDate WHERE c.id = :id")
    int updateCardById(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("number") String number,
                       @Param("holder") String holder,
                       @Param("expirationDate") LocalDate expirationDate);

    /**
     * Deletes the card with the specified ID from the database.
     *
     * @param id the ID of the card to delete
     */
    void deleteById(Long id);

    /**
     * Retrieves all cards from the database.
     *
     * @return a list of all {@link Card} entities
     */
    List<Card> findAll();

    /**
     * Retrieves a paginated list of {@link Card} entities that match the given {@link Specification}.
     * <p>
     * This method supports dynamic filtering using JPA Criteria API and returns results
     * according to the provided {@link Pageable} configuration.
     * </p>
     *
     * @param spec     the specification defining filtering criteria; can be {@code null} for no filtering
     * @param pageable the pagination and sorting information
     * @return a {@link Page} of {@link Card} entities matching the specification
     */
    Page<Card> findAll(Specification<Card> spec, Pageable pageable);
}

