--liquibase formatted sql

--changeset dev:6
ALTER TABLE post
    MODIFY writer_id BIGINT NOT NULL;
