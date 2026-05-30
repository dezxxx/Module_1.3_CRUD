CREATE TABLE post
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    title     VARCHAR(255),
    content   TEXT,
    created   TIMESTAMP,
    updated   TIMESTAMP,
    status    VARCHAR(30),
    writer_id BIGINT                NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_post_writer FOREIGN KEY (writer_id) REFERENCES writer (id) ON DELETE CASCADE
);