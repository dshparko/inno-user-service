package com.innowise.userservice.service.integration;

import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.database.repository.UserRepository;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.http.exception.ResourceNotFoundException;
import com.innowise.userservice.service.config.IntegrationTestBase;
import com.innowise.userservice.service.impl.UserService;
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
class UserServiceTestIT extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create user integration test")
    void createUser_ShouldPersistUser() {
        UserDto request = new UserDto(
                1L,
                "Darya",
                "Shparko",
                "darya@example.com",
                LocalDate.of(1990, 1, 1),
                List.of()
        );

        UserDto response = userService.create(request);

        assertNotNull(response.id());
        assertEquals("Darya", response.name());

        User persisted = userRepository.findById(response.id())
                .orElseThrow(() -> new ResourceNotFoundException("User", response.id()));

        assertEquals("darya@example.com", persisted.getEmail());
    }

    @Test
    @DisplayName("Find user by id integration test")
    void findById_ShouldReturnUser() {
        User user = new User();
        user.setName("Maria");
        user.setSurname("Kozlova");
        user.setEmail("maria@example.com");
        user.setBirthDate(LocalDate.of(1992, 7, 7));
        User saved = userRepository.save(user);

        UserDto response = userService.findById(saved.getId());

        assertEquals("Maria", response.name());
        assertEquals("maria@example.com", response.email());
    }

    @Test
    @DisplayName("Update user integration test")
    void updateUser_ShouldUpdateSuccessfully() {
        User user = new User();
        user.setName("Ivan");
        user.setSurname("Petrov");
        user.setEmail("ivan@example.com");
        user.setBirthDate(LocalDate.of(1985, 5, 5));
        User saved = userRepository.save(user);

        UserDto request = new UserDto(
                saved.getId(),
                "IvanUpdated",
                "PetrovUpdated",
                "ivan.new@example.com",
                LocalDate.of(1985, 5, 5),
                List.of()
        );

        userService.update(request);

        User updated = userRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", saved.getId()));

        assertEquals("IvanUpdated", updated.getName());
        assertEquals("ivan.new@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("Delete user integration test")
    void deleteUser_ShouldRemoveUser() {
        User user = new User();
        user.setName("Alex");
        user.setSurname("Smirnov");
        user.setEmail("alex@example.com");
        user.setBirthDate(LocalDate.of(1995, 3, 3));
        User saved = userRepository.save(user);

        userService.delete(saved.getId());

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Find all users integration test")
    void findAll_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setName("Oleg");
        user1.setSurname("Sidorov");
        user1.setEmail("oleg@example.com");
        user1.setBirthDate(LocalDate.of(1980, 10, 10));

        User user2 = new User();
        user2.setName("Anna");
        user2.setSurname("Ivanova");
        user2.setEmail("anna@example.com");
        user2.setBirthDate(LocalDate.of(1991, 12, 12));

        userRepository.saveAll(List.of(user1, user2));

        List<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
    }
}
