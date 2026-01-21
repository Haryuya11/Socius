-- Schema for Socius messaging tables

CREATE TABLE conversation_participants
(
    id                   bigserial
        PRIMARY KEY,
    conversation_id      varchar(50) NOT NULL,
    employee_id          varchar(50) NOT NULL,
    role                 varchar(20) DEFAULT 'MEMBER'::character varying,
    joined_at            timestamp   DEFAULT CURRENT_TIMESTAMP,
    left_at              timestamp,
    last_read_message_id varchar(50),
    last_read_at         timestamp,
    is_muted             boolean     DEFAULT FALSE,
    is_pinned            boolean     DEFAULT FALSE,
    created_at           timestamp   DEFAULT CURRENT_TIMESTAMP,
    updated_at           timestamp   DEFAULT CURRENT_TIMESTAMP,
    deleted_at           timestamp,
    delete_flag          smallint    DEFAULT 0,
    CONSTRAINT conversation_participants_conversation_id_employee_id_delet_key
        UNIQUE (conversation_id, employee_id, delete_flag)
);

CREATE TABLE conversations
(
    id              bigserial
        PRIMARY KEY,
    conversation_id varchar(50) NOT NULL
        UNIQUE,
    type            varchar(20) NOT NULL,
    name            varchar(200),
    avatar_url      text,
    created_by      varchar(50) NOT NULL,
    last_message_id varchar(50),
    last_message_at timestamp,
    created_at      timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp DEFAULT CURRENT_TIMESTAMP,
    deleted_at      timestamp,
    delete_flag     smallint  DEFAULT 0
);

CREATE TABLE message_reactions
(
    id          bigserial
        PRIMARY KEY,
    message_id  varchar(50) NOT NULL,
    employee_id varchar(50) NOT NULL,
    reaction    varchar(20) NOT NULL,
    created_at  timestamp DEFAULT CURRENT_TIMESTAMP,
    delete_flag smallint  DEFAULT 0,
    CONSTRAINT message_reactions_message_id_employee_id_reaction_delete_fl_key
        UNIQUE (message_id, employee_id, reaction, delete_flag)
);

CREATE TABLE messages
(
    id                bigserial
        PRIMARY KEY,
    message_id        varchar(50) NOT NULL
        UNIQUE,
    conversation_id   varchar(50) NOT NULL,
    sender_id         varchar(50) NOT NULL,
    content           text,
    message_type      varchar(20) NOT NULL,
    parent_message_id varchar(50),
    metadata_json     jsonb,
    is_edited         boolean   DEFAULT FALSE,
    edited_at         timestamp,
    created_at        timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at        timestamp DEFAULT CURRENT_TIMESTAMP,
    deleted_at        timestamp,
    delete_flag       smallint  DEFAULT 0
);
