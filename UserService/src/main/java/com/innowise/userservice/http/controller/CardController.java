package com.innowise.userservice.http.controller;

import com.innowise.userservice.dto.CardDto;
import com.innowise.userservice.dto.CardFilterDto;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.service.CardCrudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CardController
 * @Description REST controller for managing {@link com.innowise.userservice.database.entity.Card} entities and their associated {@link CardDto} data.
 * Provides endpoints for user CRUD operations and card retrieval.
 * @Author dshparko
 * @Date 12.09.2025 20:49
 * @Version 1.0
 */
@Validated
@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardCrudService cardService;

    @GetMapping("/{id}")
    @PreAuthorize(value = "hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CardDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.findById(id));
    }

    /**
     * Retrieves users based on optional filters and pagination parameters.
     *
     * @param filter   optional filter criteria (email, id, ids)
     * @param pageable pagination and sorting configuration
     * @return paginated list of {@link UserDto} objects; 204 No Content if none found
     */
    @GetMapping
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<Page<CardDto>> search(CardFilterDto filter,
                                                @PageableDefault Pageable pageable) {
        Page<CardDto> page;

        if (filter.userId() != null) {
            List<CardDto> cards = cardService.findByUserId(filter.userId());
            page = new PageImpl<>(cards, pageable, cards.size());
        } else {
            page = cardService.findAll(filter, pageable);
        }

        return page.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(page);
    }


    /**
     * Creates a new card.
     *
     * @param request DTO containing card creation data
     * @return {@link CardDto} representing the newly created card
     */
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CardDto request) {
        return ResponseEntity.ok(cardService.create(request));
    }

    /**
     * Updates an existing card.
     *
     * @param request DTO containing updated card data
     * @return 200 OK if update was successful
     */
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<CardDto> updateCard(@RequestBody @Valid CardDto request) {
        cardService.update(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a card by its ID.
     *
     * @param id the ID of the card to delete
     * @return 200 OK if deletion was successful
     */
    @PreAuthorize(value = "hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
