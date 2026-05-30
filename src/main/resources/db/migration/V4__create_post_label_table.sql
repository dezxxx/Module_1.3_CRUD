CREATE TABLE post_label
(
    post_id  BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, label_id),
    CONSTRAINT fk_post_label_post  FOREIGN KEY (post_id)  REFERENCES post  (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_label_label FOREIGN KEY (label_id) REFERENCES label (id) ON DELETE CASCADE
);
