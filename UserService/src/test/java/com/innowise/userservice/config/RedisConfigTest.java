package com.innowise.userservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = RedisConfig.class)
class RedisConfigTest {
    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private ObjectMapper redisObjectMapper;

    @Test
    void shouldSerializeAndDeserializeUserDtoCorrectly() throws Exception {
        UserDto dto = new UserDto(1L, "Darya", "Shparko", "darya@example.com", LocalDate.of(1995, 1, 1), List.of());

        String json = redisObjectMapper.writeValueAsString(dto);
        UserDto deserialized = redisObjectMapper.readValue(json, UserDto.class);

        assertThat(deserialized.id()).isEqualTo(1L);
        assertThat(deserialized.name()).isEqualTo("Darya");
        assertThat(deserialized.email()).isEqualTo("darya@example.com");
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String json = """
            {
              "id": 2,
              "name": "Ivan",
              "surname": "Ivanov",
              "email": "ivan@example.com",
              "birthDate": "1990-05-15",
              "unknownField": "ignored"
            }
            """;

        UserDto dto = redisObjectMapper.readValue(json, UserDto.class);
        assertThat(dto.name()).isEqualTo("Ivan");
    }
}