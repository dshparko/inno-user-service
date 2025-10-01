package com.innowise.userservice.service.integration;

import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.database.repository.UserRepository;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.service.config.IntegrationTestBase;
import com.innowise.userservice.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceCacheTestIT extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setName("Darya");
        user.setSurname("Shparko");
        user.setEmail("darya@example.com");
        user.setBirthDate(of(1990, 1, 1));
        savedUser = userRepository.save(user);

        Cache userWithCards = cacheManager.getCache("userWithCards");
        if (userWithCards != null) {
            userWithCards.clear();
        }

        Cache userByEmail = cacheManager.getCache("userByEmail");
        if (userByEmail != null) {
            userByEmail.clear();
        }
    }

    @Test
    @DisplayName("findById should cache result")
    void findById_ShouldCacheResult() {
        UserDto response1 = userService.findById(savedUser.getId());

        assertEquals("Darya", response1.getName());

        savedUser.setName("Changed");
        userRepository.save(savedUser);

        UserDto response2 = userService.findById(savedUser.getId());
        assertEquals("Darya", response2.getName());
    }

    @Test
    @DisplayName("updateUser should evict cache")
    void updateUser_ShouldEvictCache() {
        userService.findById(savedUser.getId());

        UserDto updateRequest = new UserDto(
                savedUser.getId(),
                "Ivan",
                "Ivanov",
                "ivan@example.com",
                of(1995, 5, 5),
                List.of()
        );

        userService.update(updateRequest);

        UserDto response = userService.findById(savedUser.getId());
        assertEquals("Ivan", response.getName());
        assertEquals("Ivanov", response.getSurname());
    }

    @Test
    @DisplayName("deleteUser should evict cache")
    void deleteUser_ShouldEvictCache() {
        userService.findById(savedUser.getId());

        userService.delete(savedUser.getId());

        assertFalse(userRepository.findById(savedUser.getId()).isPresent());

        long userId = savedUser.getId();
        assertThrows(RuntimeException.class, () -> userService.findById(userId));
    }
}
