package com.innowise.userservice.service.integration.specification;

import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.database.repository.UserRepository;
import com.innowise.userservice.database.specification.UserSpecification;
import com.innowise.userservice.dto.UserFilterDto;
import com.innowise.userservice.mapper.UserMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(UserMapperImpl.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByIds() {
        User user1 = new User();
        User user2 = new User();

        user1.setName("Darya");
        user1.setSurname("Shparko");
        user1.setEmail("darya@example.com");
        user1.setBirthDate(LocalDate.of(1995, 1, 1));

        user2.setName("Ivan");
        user2.setSurname("Ivanoc");
        user2.setEmail("ivan@example.com");
        user2.setBirthDate(LocalDate.of(1992, 2, 3));

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        UserFilterDto filter = new UserFilterDto(null, null, List.of(user1.getId(), user2.getId()));

        List<User> result = userRepository.findAll(UserSpecification.from(filter));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getEmail)
                .containsExactlyInAnyOrder("darya@example.com", "ivan@example.com");
    }
}
