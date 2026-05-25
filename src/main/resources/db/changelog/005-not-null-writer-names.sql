--liquibase formatted sql

--changeset dev:5
ALTER TABLE writer
    MODIFY first_name VARCHAR(255) NOT NULL,
    MODIFY last_name  VARCHAR(255) NOT NULL;
