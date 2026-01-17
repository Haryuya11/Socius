-- Schema for H2 Database (Test)
DROP TABLE IF EXISTS departments CASCADE;

CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(255) NOT NULL UNIQUE,
    department_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    delete_flag INTEGER DEFAULT 0
);
