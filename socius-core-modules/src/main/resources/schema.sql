CREATE TABLE employee
(
    id              SERIAL PRIMARY KEY,
    client_id       VARCHAR(50) UNIQUE NOT NULL,
    user_id         VARCHAR(50) UNIQUE NOT NULL,
    first_name      VARCHAR(50)        NOT NULL,
    last_name       VARCHAR(50)        NOT NULL,
    team_code       VARCHAR(10)        NOT NULL,
    department_code VARCHAR(10)        NOT NULL,
    role_code       VARCHAR(10)        NOT NULL,
    image_url       TEXT,
    salary          BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0
);

create table teams
(
    id          SERIAL PRIMARY KEY,
    team_code   VARCHAR(10) UNIQUE NOT NULL,
    team_name   VARCHAR(100)       NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    delete_flag SMALLINT  DEFAULT 0
);

create table departments
(
    id              SERIAL PRIMARY KEY,
    department_code VARCHAR(10) UNIQUE NOT NULL,
    department_name VARCHAR(100)       NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0
);

create table roles
(
    id          SERIAL PRIMARY KEY,
    role_code   VARCHAR(10) UNIQUE NOT NULL,
    role_name   VARCHAR(100)       NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    delete_flag SMALLINT  DEFAULT 0
);

create table tasks
(
    id            SERIAL PRIMARY KEY,
    receiver_id   SERIAL NOT NULL,
    sender_id     SERIAL NOT NULL,
    payload       TEXT   NOT NULL,
    status        SMALLINT  DEFAULT 0,
    delivery_type SMALLINT  DEFAULT 0,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP,
    delete_flag   SMALLINT  DEFAULT 0
);
