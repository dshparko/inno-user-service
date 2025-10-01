--liquibase formatted sql

--changeset dshparko:1
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(64)  NOT NULL,
    surname    VARCHAR(64)  NOT NULL,
    email      VARCHAR(128) NOT NULL,
    birth_date DATE         NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email)
);

--changeset dshparko:2
CREATE TABLE IF NOT EXISTS card_info
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    number          VARCHAR(19)  NOT NULL,
    holder          VARCHAR(128) NOT NULL,
    expiration_date DATE         NOT NULL,
    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_card_number UNIQUE (number),
    CONSTRAINT chk_card_number CHECK (number ~ '^[0-9]{13,19}$')
);
CREATE INDEX IF NOT EXISTS idx_card_info_user_id ON card_info (user_id);
