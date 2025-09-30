package com.innowise.userservice.service.unit.specification;

import com.innowise.userservice.database.entity.Card;
import com.innowise.userservice.database.repository.CardRepository;
import com.innowise.userservice.database.specification.CardSpecification;
import com.innowise.userservice.dto.CardDto;
import com.innowise.userservice.dto.CardFilterDto;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.mapper.CardMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(CardMapperImpl.class)
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CardMapper cardMapper;

    @BeforeEach
    void cleanUp() {
        cardRepository.deleteAll();
    }

    @Test
    void testFindByUserId() {
        CardDto dto = new CardDto(1L, "1234567890123", "Ivan Ivanov", LocalDate.now().plusYears(1), 1L);
        Card card1 = cardMapper.mapToEntity(dto);

        cardRepository.save(card1);

        CardFilterDto filter = new CardFilterDto(null, 1L, null);

        List<Card> result = cardRepository.findAll(CardSpecification.from(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(1L);
    }

    @Test
    void testFindByIds() {
        CardDto dto = new CardDto(1L, "1234567890123", "Ivan Ivanov", LocalDate.now().plusYears(1), 1L);
        CardDto dto2 = new CardDto(2L, "1234532490123", "Valentin Valen", LocalDate.now().plusYears(1), 2L);

        Card card1 = cardRepository.save(cardMapper.mapToEntity(dto));
        Card card2 = cardRepository.save(cardMapper.mapToEntity(dto2));
        CardFilterDto filter = new CardFilterDto(null, null, List.of(card1.getId(), card2.getId()));

        List<Card> result = cardRepository.findAll(CardSpecification.from(filter));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(card1.getId());
    }
}
