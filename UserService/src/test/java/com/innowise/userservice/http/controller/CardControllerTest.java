package com.innowise.userservice.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.CardDto;
import com.innowise.userservice.service.impl.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private final CardDto sampleCard = new CardDto(
            1L,
            "1234567890123456",
            "Ivan Ivanov",
            LocalDate.of(2030, 12, 31),
            42L
    );

    @Test
    @DisplayName("GET /cards/{id} should return card by ID")
    void getCardById_shouldReturnCard() throws Exception {
        Mockito.when(cardService.findById(1L)).thenReturn(sampleCard);

        mockMvc.perform(get("/api/v1/cards?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].number").value("1234567890123456"));
    }

    @Test
    @DisplayName("GET /cards should return all cards")
    void findAll_shouldReturnList() throws Exception {
        Mockito.when(cardService.findAll(any(), any())).thenReturn(new PageImpl<>(List.of(sampleCard)));

        mockMvc.perform(get("/api/v1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /cards should return 204 if empty")
    void findAll_shouldReturnNoContent() throws Exception {
        Mockito.when(cardService.findAll(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/cards"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /cards/batch should return cards by IDs")
    void findCardsByIds_shouldReturnList() throws Exception {
        Mockito.when(cardService.findAll(Mockito.any(), Mockito.any())).thenReturn(new PageImpl<>(List.of(sampleCard)));

        mockMvc.perform(get("/api/v1/cards?ids=1&ids=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("POST /cards should create a card")
    void createCard_shouldReturnCreatedCard() throws Exception {
        CardDto request = new CardDto(
                1L,
                "1234567890123456",
                "Ivan Ivanov",
                LocalDate.of(2030, 12, 31),
                42L
        );

        Mockito.when(cardService.create(any())).thenReturn(sampleCard);

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /cards should update a card")
    void updateCard_shouldReturnIsOk() throws Exception {
        CardDto request = new CardDto(
                1L,
                "9999888877776666",
                "Ivan Ivanov",
                LocalDate.of(2031, 1, 1),
                1L
        );

        mockMvc.perform(put("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).update(request);
    }

    @Test
    @DisplayName("DELETE /cards/{id} should delete card")
    void deleteCard_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(cardService).delete(1L);
    }

    @Test
    @DisplayName("GET /cards with userId and ids should return filtered cards")
    void findCardsByUserIdAndIds_shouldReturnFilteredList() throws Exception {
        List<CardDto> cards = List.of(
                new CardDto(10L, "1234123412341234", "Darya", LocalDate.of(2026, 1, 1), 5L),
                new CardDto(11L, "5678567856785678", "Ivan", LocalDate.of(2026, 6, 1), 5L)
        );

        Mockito.when(cardService.findAll(Mockito.any(), Mockito.any())).thenReturn(new PageImpl<>(cards));

        mockMvc.perform(get("/api/v1/cards")
                        .param("userid", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[1].id").value(11));
    }

}
