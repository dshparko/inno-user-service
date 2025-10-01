package com.innowise.userservice.service.integration;

import com.innowise.userservice.database.entity.Card;
import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.database.repository.CardRepository;
import com.innowise.userservice.database.repository.UserRepository;
import com.innowise.userservice.dto.CardDto;
import com.innowise.userservice.http.exception.ResourceNotFoundException;
import com.innowise.userservice.service.config.IntegrationTestBase;
import com.innowise.userservice.service.impl.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CardServiceTestIT extends IntegrationTestBase {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    void cleanDatabase() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create card integration test")
    void createCard_ShouldPersistCard() {
        User user = new User();
        user.setName("Darya");
        user.setSurname("Shparko");
        user.setEmail("darya@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        User savedUser = userRepository.save(user);

        CardDto request = new CardDto(
                1L,
                "1111222233334444",
                "Darya",
                LocalDate.of(2030, 1, 1),
                savedUser.getId()
        );

        CardDto response = cardService.create(request);

        assertNotNull(savedUser.getId());
        assertEquals("1111222233334444", response.getNumber());

        Card persisted = cardRepository.findById(response.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Card", response.getId()));

        assertEquals("Darya", persisted.getHolder());
        assertEquals(savedUser.getId(), persisted.getUser().getId());
    }

    @Test
    @DisplayName("Update card integration test")
    void updateCard_ShouldUpdateData() {
        User user = new User();
        user.setName("Ivan");
        user.setSurname("Ivanov");
        user.setEmail("ivan@example.com");
        user.setBirthDate(LocalDate.of(1985, 5, 20));
        userRepository.save(user);

        Card card = new Card();
        card.setNumber("5555666677778888");
        card.setHolder("Ivan");
        card.setExpirationDate(LocalDate.of(2030, 1, 1));
        card.setUser(user);
        cardRepository.save(card);

        CardDto request = new CardDto(
                card.getId(),
                "9999000011112222",
                "Ivan Updated",
                LocalDate.of(2040, 12, 31),
                user.getId()
        );

        cardService.update(request);

        Card updated = cardRepository.findById(card.getId()).orElseThrow();

        assertEquals("9999000011112222", updated.getNumber());
        assertEquals("Ivan Updated", updated.getHolder());
    }

    @Test
    @DisplayName("Delete card integration test")
    void deleteCard_ShouldRemoveCard() {
        User user = new User();
        user.setName("Alex");
        user.setSurname("Petrov");
        user.setEmail("alex@example.com");
        user.setBirthDate(LocalDate.of(1995, 3, 15));
        userRepository.save(user);

        Card card = new Card();
        card.setNumber("4444555566667777");
        card.setHolder("Alex");
        card.setExpirationDate(LocalDate.of(2029, 5, 10));
        card.setUser(user);
        cardRepository.save(card);

        cardService.delete(card.getId());

        assertFalse(cardRepository.findById(card.getId()).isPresent());
    }

    @Test
    @DisplayName("Find all cards integration test")
    void findAll_ShouldReturnList() {
        User user = new User();
        user.setName("Maria");
        user.setSurname("Kozlova");
        user.setEmail("maria@example.com");
        user.setBirthDate(LocalDate.of(1992, 7, 7));
        userRepository.save(user);

        Card card1 = new Card();
        card1.setNumber("1234123412341234");
        card1.setHolder("Maria");
        card1.setExpirationDate(LocalDate.of(2031, 1, 1));
        card1.setUser(user);

        Card card2 = new Card();
        card2.setNumber("5678567856785678");
        card2.setHolder("Maria");
        card2.setExpirationDate(LocalDate.of(2032, 1, 1));
        card2.setUser(user);

        cardRepository.saveAll(List.of(card1, card2));

        List<CardDto> result = cardService.findAll();

        assertEquals(2, result.size());
    }
}
