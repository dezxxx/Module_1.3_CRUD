CREATE TABLE label
(
    id   BIGSERIAL    NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_label_name UNIQUE (name)
);