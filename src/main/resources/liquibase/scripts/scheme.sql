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

-- changeSet 11th:4
;
ALTER TABLE animals
    DROP COLUMN days_for_test,
    DROP COLUMN end_test;

ALTER TABLE users
    ADD COLUMN animal_id BIGINT REFERENCES animals (id),
    ADD COLUMN days_for_test INT,
    ADD COLUMN end_test TIMESTAMP,
    DROP COLUMN end_trial_period;

-- changeSet 11th:5
ALTER TABLE animals
    DROP COLUMN status;

-- changeSet 11th:6
ALTER TABLE reports
    DROP COLUMN description,
    DROP COLUMN status,
    ALTER photo DROP NOT NULL,
    ALTER diet DROP NOT NULL,
    ALTER well_being DROP NOT NULL,
    ALTER change_behavior DROP NOT NULL;

--changeSet slyubimov:5
ALTER TABLE users
    RENAME COLUMN is_volunteer TO volunteer;

--changeSet slyubimov:6
CREATE TABLE IF NOT EXISTS cats
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    breed         VARCHAR(50),
    description   TEXT,
    photo         OID,
    user_id       BIGINT REFERENCES users (id),
    state        VARCHAR(10),
    start_test    TIMESTAMP,
    animal_type VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS dogs
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    breed         VARCHAR(50),
    description   TEXT,
    photo         OID,
    user_id       BIGINT REFERENCES users (id),
    state        VARCHAR(10),
    start_test    TIMESTAMP,
    animal_type VARCHAR(10)
);

-- changeSet slyubimov:7
CREATE TABLE hibernate_sequences (
                                     sequence_name varchar(255) NOT NULL,
                                     next_val bigint,
                                     PRIMARY KEY (sequence_name),
                                     sequence_next_hi_value bigint);
ALTER TABLE reports
    ADD COLUMN cat_id BIGINT REFERENCES cats(id),
    ADD COLUMN dog_id BIGINT REFERENCES dogs(id);

-- changeSet 11th:7
CREATE TABLE IF NOT EXISTS shelters
(
    id                       BIGSERIAL PRIMARY KEY NOT NULL,
    name                     VARCHAR(50)           NOT NULL,
    type                     VARCHAR(10),
    address                  VARCHAR(100),
    schedule                 VARCHAR(100),
    scheme                   VARCHAR(100),
    safety                   TEXT,
    docs                     TEXT,
    rules                    TEXT,
    arrangements             TEXT,
    arrangements_for_puppy   TEXT,
    arrangements_for_cripple TEXT,
    movement                 TEXT,
    expert_advices_first     TEXT,
    expert_advices_next      TEXT,
    reject_reasons           TEXT
);

ALTER TABLE users
    ADD COLUMN shelter_id BIGINT REFERENCES shelters(id);

ALTER TABLE animals
    ADD COLUMN shelter_id BIGINT REFERENCES shelters(id);

-- changeSet slyubimov:8
ALTER TABLE reports
    DROP COLUMN dog_id,
    DROP COLUMN cat_id;

-- changSet slyubimov:9
ALTER TABLE animals
    ADD COLUMN type VARCHAR(10);

-- changSet slyubimov:10
DROP TABLE dogs;
DROP TABLE cats;
DROP TABLE hibernate_sequences;

-- changSet slyubimov:11
CREATE TABLE IF NOT EXISTS photo
(
    id                       BIGSERIAL PRIMARY KEY NOT NULL,
    file_path VARCHAR(30),
    file_size BIGINT,
    media_type TEXT,
    preview OID,
    animal_id BIGINT REFERENCES animals(id)
);

-- changSet slyubimov:12
ALTER TABLE users
    DROP COLUMN days_for_test,
ADD COLUMN days_for_test BIGINT;

-- changSet slyubimov:13
DROP TABLE photo;

-- changeSet 11th:8
ALTER TABLE animals
    DROP COLUMN photo;

ALTER TABLE animals
    ADD COLUMN photo BYTEA;

-- changeSet 11th:9
ALTER TABLE reports
    DROP COLUMN photo;

ALTER TABLE reports
    ADD COLUMN photo BYTEA;

