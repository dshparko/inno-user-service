package com.innowise.userservice.database.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName User
 * @Description Entity representing a system user with personal details such as name, surname,
 * email, and birthdate. Maintains a list of associated cards and supports cascading operations.
 * @Author dshparko
 * @Date 08.09.2025 15:30
 * @Version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "userCards")
@ToString(exclude = "userCards")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Card> userCards = new ArrayList<>();

    public void addCard(Card card) {
        if (card == null) {
            return;
        }
        userCards.add(card);
        card.setUser(this);
    }

    public void removeCard(Card card) {
        if (card == null) {
            return;
        }
        userCards.remove(card);
        card.setUser(null);
    }
}
