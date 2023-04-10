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

-- changeSet 11th:2
ALTER TABLE users
    ADD is_volunteer BOOL DEFAULT false;

-- changeSet 11th:3
ALTER TABLE reports DROP COLUMN volunteer_id;
DROP TABLE volunteers;

--changeSet slyubimov:1
ALTER TABLE users
    ADD COLUMN state VARCHAR(10);

--changeSet slyubimov:2
ALTER TABLE users
ADD COLUMN end_trial_period TIMESTAMP;

--changeSet slyubimov:3
ALTER TABLE animals drop COLUMN start_test,
    ADD COLUMN end_test TIMESTAMP;

--changeSet slyubimov:4
ALTER TABLE animals
    ADD COLUMN state VARCHAR(10)