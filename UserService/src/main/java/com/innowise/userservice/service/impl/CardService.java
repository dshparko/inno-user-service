package com.innowise.userservice.service.impl;

import com.innowise.userservice.database.entity.Card;
import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.database.repository.CardRepository;
import com.innowise.userservice.database.specification.CardSpecification;
import com.innowise.userservice.database.repository.UserRepository;
import com.innowise.userservice.dto.CardDto;
import com.innowise.userservice.dto.CardFilterDto;
import com.innowise.userservice.http.exception.ModificationException;
import com.innowise.userservice.http.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.service.CardCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CardService
 * @Description Service layer for managing {@link Card} entities.
 * Provides operations for creating, retrieving, updating, and deleting cards.
 * @Author dshparko
 * @Date 12.09.2025 20:49
 * @Version 1.0
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CardService implements CardCrudService{

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    /**
     * Creates a new card based on the provided request DTO.
     *
     * @param request DTO containing card creation data
     * @return mapped {@link CardDto} representing the saved card
     */
    @Transactional
    @Override
    public CardDto create(CardDto request) {
        Card card = cardMapper.mapToEntity(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        user.addCard(card);
        return cardMapper.mapToDto(cardRepository.save(card));
    }

    /**
     * Retrieves a card by its ID.
     *
     * @param id unique identifier of the card
     * @return mapped {@link CardDto} if found
     * @throws ResourceNotFoundException if no card exists with the given ID
     */
    @Override
    @Cacheable(value = "card", key = "#id")
    public CardDto findById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card", id));
        return cardMapper.mapToDto(card);
    }

    /**
     * Retrieves multiple cards by their IDs.
     *
     * @param ids list of card IDs to fetch
     * @return list of mapped {@link CardDto} objects
     */
    @Override
    public List<CardDto> findByIds(List<Long> ids) {
        return cardMapper.mapToDtoList(cardRepository.findCardsByIdIn(ids));
    }

    /**
     * Updates an existing card based on the provided request DTO.
     * Ensures the card exists before performing the update.
     *
     * @param request DTO containing updated card data
     * @throws ResourceNotFoundException if no card exists with the given ID
     * @throws ModificationException     if the associated user is null
     */
    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "card", key = "#request.id()")
    })
    public void update(CardDto request) {
        if (!cardRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Card", request.getId());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        int updated = cardRepository.updateCardById(
                request.getId(),
                user.getId(),
                request.getNumber(),
                request.getHolder(),
                request.getExpirationDate()
        );

        if (updated == 0) {
            throw new ModificationException("Card", request.getId());
        }
    }

    /**
     * Deletes a card by its ID.
     * Ensures the card exists before deletion.
     *
     * @param id unique identifier of the card to delete
     * @throws ResourceNotFoundException if no card exists with the given ID
     */
    @Transactional
    @Override
    @CacheEvict(value = "card", key = "#id")
    public void delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card", id));

        User user = card.getUser();
        if (user != null && user.getUserCards().contains(card)) {
            user.removeCard(card);
        }

        cardRepository.deleteById(id);
    }


    /**
     * Retrieves all cards in the system.
     *
     * @return list of mapped {@link CardDto} objects
     */
    @Override
    public List<CardDto> findAll() {
        return cardMapper.mapToDtoList(cardRepository.findAll());
    }

    @Override
    public Page<CardDto> findAll(CardFilterDto filter, Pageable pageable) {
        Page<Card> cardsPage = cardRepository.findAll(CardSpecification.from(filter), pageable);

        List<CardDto> dtoList = cardMapper.mapToDtoList(cardsPage.getContent());

        return new PageImpl<>(dtoList, pageable, cardsPage.getTotalElements());
    }

    /**
     * Retrieves all cards for the selected user by id
     *
     * @param userId - Users id
     * @return list of mapped {@link CardDto} objects
     */
    @Override
    public List<CardDto> findByUserId(Long userId) {
        List<Card> cards = cardRepository.findCardsByUserId(userId);

        return cardMapper.mapToDtoList(cards);
    }
}
