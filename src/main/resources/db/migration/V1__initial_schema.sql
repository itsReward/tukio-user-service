-- Path: src/main/resources/db/migration/V1__initial_schema.sql

-- This is an initial migration that will establish Flyway's control over the schema
-- Using IF NOT EXISTS to avoid errors if tables already exist

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
    );

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_picture_url VARCHAR(255),
    bio TEXT,
    department VARCHAR(100),
    student_id VARCHAR(50),
    graduation_year INT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- User Roles join table
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
    );

-- User Interests table
CREATE TABLE IF NOT EXISTS user_interests (
                                              user_id BIGINT NOT NULL REFERENCES users(id),
    interest VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, interest)
    );

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_department ON users(department);
CREATE INDEX IF NOT EXISTS idx_user_interests_interest ON user_interests(interest);

-- Initial roles data
INSERT INTO roles (name) VALUES
                             ('USER'),
                             ('ADMIN'),
                             ('EVENT_ORGANIZER'),
                             ('FACULTY'),
                             ('STUDENT')
    ON CONFLICT (name) DO NOTHING;

-- Create admin user with password 'admin123' (bcrypt encoded)
INSERT INTO users (
    username,
    email,
    password,
    first_name,
    last_name
) VALUES (
             'admin',
             'admin@tukio.com',
             '$2a$10$aYB3.2VQQtAJpKl0KQ5kN.ZDTQ5uUazqenD.d5mVU9r6LGjfPYsLO',
             'System',
             'Administrator'
         )
    ON CONFLICT (username) DO NOTHING;

-- Assign admin role to admin user
DO $
DECLARE
admin_user_id BIGINT;
    admin_role_id BIGINT;
BEGIN
SELECT id INTO admin_user_id FROM users WHERE username = 'admin';
SELECT id INTO admin_role_id FROM roles WHERE name = 'ADMIN';

IF admin_user_id IS NOT NULL AND admin_role_id IS NOT NULL THEN
        INSERT INTO user_roles (user_id, role_id)
        VALUES (admin_user_id, admin_role_id)
        ON CONFLICT (user_id, role_id) DO NOTHING;
END IF;
END
$;