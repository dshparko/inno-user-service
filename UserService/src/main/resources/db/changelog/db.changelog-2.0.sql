--liquibase formatted sql

--changeset dshparko:1
INSERT INTO users (name, surname, email, birth_date)
VALUES ('Alice', 'Ivanova', 'alice@example.com', '1990-05-12'),
       ('Bob', 'Petrov', 'bob@example.com', '1985-11-23'),
       ('Charlie', 'Sidorov', 'charlie@example.com', '1992-07-08');

--changeset dshparko:2
INSERT INTO card_info (user_id, number, holder, expiration_date)
VALUES (1, '12345678901234', 'Alice Ivanova', '2026-12-31'),
       (2, '2345678901234567', 'Bob Petrov', '2025-06-30'),
       (3, '345678901235678', 'Charlie Sidorov', '2027-03-15'),
       (3, '345678901234568887', 'Charlie Sidorov', '2026-03-15');