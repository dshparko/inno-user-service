package com.innowise.userservice.database.specification;


import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.dto.UserFilterDto;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    /**
     * Builds a dynamic JPA {@link Specification} for filtering {@link User} entities
     * based on optional fields provided in {@link UserFilterDto}.
     *
     * @param filter the filter DTO containing optional criteria
     * @return a composed {@link Specification} for querying users
     */
    public static Specification<User> from(UserFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.ids() != null && !filter.ids().isEmpty()) {
                predicates.add(root.get("id").in(filter.ids()));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

