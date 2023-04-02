-- liquibase formatted sql

-- changeSet 11th:1
CREATE TABLE IF NOT EXISTS users
(
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    telegram_id BIGINT                NOT NULL,
    name        VARCHAR(50),
    surname     VARCHAR(50),
    phone       VARCHAR(50),
    email       VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS animals
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    breed         VARCHAR(50),
    description   TEXT,
    photo         OID,
    user_id       BIGINT REFERENCES users (id),
    status        VARCHAR(10),
    start_test    TIMESTAMP,
    days_for_test INT
    );

CREATE TABLE IF NOT EXISTS volunteers
(
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    telegram_id BIGINT                NOT NULL,
    name        VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS reports
(
    id              BIGSERIAL PRIMARY KEY          NOT NULL,
    user_id         BIGINT REFERENCES users (id)   NOT NULL,
    animal_id       BIGINT REFERENCES animals (id) NOT NULL,
    volunteer_id    BIGINT REFERENCES volunteers (id),
    description     TEXT                           NOT NULL,
    status          VARCHAR(10)                    NOT NULL,
    date            TIMESTAMP                      NOT NULL,
    photo           OID                            NOT NULL,
    diet            TEXT                           NOT NULL,
    well_being      TEXT                           NOT NULL,
    change_behavior TEXT                           NOT NULL
    );