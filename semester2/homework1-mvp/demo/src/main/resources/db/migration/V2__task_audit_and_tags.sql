ALTER TABLE tasks
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE task_tags (
    task_id BIGINT NOT NULL,
    tag VARCHAR(64) NOT NULL,
    PRIMARY KEY (task_id, tag),
    CONSTRAINT fk_task_tags_task
      FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);
