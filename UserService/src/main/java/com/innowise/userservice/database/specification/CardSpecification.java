package com.innowise.userservice.database.specification;

import com.innowise.userservice.database.entity.Card;
import com.innowise.userservice.dto.CardFilterDto;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardSpecification {
    /**
     * Builds a dynamic JPA {@link Specification} for filtering {@link Card} entities
     * based on optional fields provided in {@link CardFilterDto}.
     *
     * @param filter the filter DTO containing optional criteria
     * @return a composed {@link Specification} for querying users
     */
    public static Specification<Card> from(CardFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.userId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
            }

            if (filter.ids() != null && !filter.ids().isEmpty()) {
                predicates.add(root.get("id").in(filter.ids()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
