package com.innowise.userservice.mapper;

import com.innowise.userservice.database.entity.Card;
import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("mapToResponse should map User to UserResponse correctly")
    void mapToResponse_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("Darya");
        user.setSurname("Shparko");
        user.setEmail("darya@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        UserDto response = userMapper.mapToDto(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Darya");
        assertThat(response.getSurname()).isEqualTo("Shparko");
        assertThat(response.getEmail()).isEqualTo("darya@example.com");
        assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("mapToEntity should map CreateUserRequest to User correctly")
    void mapToEntity_shouldMapCorrectly() {
        UserDto request = new UserDto(
                1L,
                "Darya",
                "Shparko",
                "darya@example.com",
                LocalDate.of(1990, 1, 1),
                List.of()
        );

        User user = userMapper.mapToEntity(request);

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("Darya");
        assertThat(user.getSurname()).isEqualTo("Shparko");
        assertThat(user.getEmail()).isEqualTo("darya@example.com");
        assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(user.getUserCards().isEmpty());
    }

    @Test
    @DisplayName("mapToUserWithCards should map User with cards correctly")
    void mapToUserWithCards_shouldMapCorrectly() {
        Card card = new Card();
        card.setId(101L);
        card.setNumber("1234567891011124");
        card.setExpirationDate(LocalDate.of(2030, 12, 31));

        User user = new User();
        user.setId(1L);
        user.setName("Darya");
        user.setSurname("Shparko");
        user.setEmail("darya@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setUserCards(List.of(card));

        UserDto response = userMapper.mapToDto(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCards().size() == 1);
        assertThat(response.getCards().getFirst().getNumber()).isEqualTo("1234567891011124");
    }

    @Test
    @DisplayName("mapToUserWithCardsResponseList should map list of Users correctly")
    void mapToUserWithCardsResponseList_shouldMapListCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("Darya");
        user.setSurname("Shparko");
        user.setEmail("darya@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setUserCards(List.of());

        List<UserDto> responses = userMapper.mapToDtoList(List.of(user));

        assertThat(responses.size() == 1);
        assertThat(responses.getFirst().getEmail()).isEqualTo("darya@example.com");
    }
}