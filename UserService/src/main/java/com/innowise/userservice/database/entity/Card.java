package com.innowise.userservice.database.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * @ClassName Card
 * @Description Entity representing card information linked to a user. Includes card number,
 * holder name, expiration date, and a reference to the owning user.
 * @Author dshparko
 * @Date 08.09.2025 15:36
 * @Version 1.0
 */
@Entity
@Table(name = "card_info")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String number;

    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}
