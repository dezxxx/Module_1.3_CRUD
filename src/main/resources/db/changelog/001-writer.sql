--liquibase formatted sql

--changeset dev:1
CREATE TABLE writer
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    PRIMARY KEY (id)
);
