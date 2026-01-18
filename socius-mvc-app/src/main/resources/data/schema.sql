CREATE TABLE IF NOT EXISTS employees
(
    id          SERIAL PRIMARY KEY,
    client_id   VARCHAR(50) UNIQUE NOT NULL,
    user_id     VARCHAR(50) UNIQUE NOT NULL,
    first_name  VARCHAR(50)        NOT NULL,
    last_name   VARCHAR(50)        NOT NULL,
    system_role VARCHAR(20)        NOT NULL,
    image_url   TEXT,
    salary      BIGINT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    delete_flag SMALLINT  DEFAULT 0
);

CREATE TABLE IF NOT EXISTS team_employees
(
    id          SERIAL PRIMARY KEY,
    employee_id VARCHAR(50)     NOT NULL,
    team_code   VARCHAR(10) NOT NULL,
    role_code   VARCHAR(10) NOT NULL,
    is_leader   BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    delete_flag SMALLINT  DEFAULT 0,
    UNIQUE (employee_id, team_code, delete_flag)
);

CREATE TABLE IF NOT EXISTS teams
(
    id              SERIAL PRIMARY KEY,
    team_code       VARCHAR(10) UNIQUE NOT NULL,
    team_name       VARCHAR(100)       NOT NULL,
    department_code VARCHAR(10)        NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0
);

CREATE TABLE IF NOT EXISTS departments
(
    id              SERIAL PRIMARY KEY,
    department_code VARCHAR(10) UNIQUE NOT NULL,
    department_name VARCHAR(100)       NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0
);

CREATE TABLE IF NOT EXISTS roles
(
    id          SERIAL PRIMARY KEY,
    role_code   VARCHAR(10) UNIQUE NOT NULL,
    role_name   VARCHAR(100)       NOT NULL,
    role_type   VARCHAR(20)        NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    delete_flag SMALLINT  DEFAULT 0
);

CREATE TABLE IF NOT EXISTS permissions
(
    id              SERIAL PRIMARY KEY,
    permission_code VARCHAR(50) UNIQUE NOT NULL,
    permission_name VARCHAR(100)       NOT NULL,
    resource        VARCHAR(50)        NOT NULL,
    action          VARCHAR(20)        NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0
);

CREATE TABLE IF NOT EXISTS role_permissions
(
    id              SERIAL PRIMARY KEY,
    role_code       VARCHAR(10) NOT NULL,
    permission_code VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0,
    UNIQUE (role_code, permission_code)
);

-- Quan hệ Employee <-> Department
CREATE TABLE IF NOT EXISTS department_employees
(
    id              SERIAL PRIMARY KEY,
    employee_id     VARCHAR(50)     NOT NULL,
    department_code VARCHAR(10) NOT NULL,
    role_code       VARCHAR(10) NOT NULL,
    is_primary      BOOLEAN   DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0,
    UNIQUE (employee_id, department_code, delete_flag)
);

CREATE TABLE IF NOT EXISTS tasks
(
    id               SERIAL PRIMARY KEY,
    parent_id        INTEGER NULL,
    receiver_id      VARCHAR(50) NOT NULL,
    sender_id        VARCHAR(50) NOT NULL,
    team_code        VARCHAR(10) NOT NULL,
    department_code  VARCHAR(10) NOT NULL,
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    delivery_type    SMALLINT DEFAULT 0,
    status           SMALLINT DEFAULT 0,   -- 0=IN_PROGRESS, 1=PENDING, 2=APPROVED, 3=REJECTED, 4=OVERDUE, 5=CANCELLED
    priority         SMALLINT DEFAULT 1,   -- 0=LOW, 1=MEDIUM, 2=HIGH
    start_date       TIMESTAMP NOT NULL,
    due_date         TIMESTAMP NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP,
    delete_flag      SMALLINT DEFAULT 0
);

-- Task Activities History
CREATE TABLE IF NOT EXISTS task_activities
(
    id              SERIAL PRIMARY KEY,
    task_id         INTEGER NOT NULL,
    activity_type   SMALLINT NOT NULL,           -- 0=SUBMIT, 1=APPROVE, 2=REJECT, 3=CANCEL, 4=REOPEN
    actor_id        VARCHAR(50) NOT NULL,
    note            TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications
(
    id            BIGSERIAL PRIMARY KEY,
    receiver_id   VARCHAR(50) NOT NULL,
    delivery_type SMALLINT    NOT NULL,
    payload_json  TEXT        NOT NULL,
    is_read       SMALLINT  DEFAULT 0,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP,
    delete_flag   SMALLINT  DEFAULT 0
);

-- API Permissions for DB-driven RBAC authorization
CREATE TABLE IF NOT EXISTS api_permissions
(
    id              SERIAL PRIMARY KEY,
    permission_code VARCHAR(50) NOT NULL,
    resource        VARCHAR(50) NOT NULL,
    action          VARCHAR(20) NOT NULL,
    http_method     VARCHAR(10) NOT NULL,
    url_pattern     VARCHAR(200) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    delete_flag     SMALLINT  DEFAULT 0,
    UNIQUE (http_method, url_pattern, delete_flag)
);