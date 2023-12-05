--Create Status Table and link with File table

CREATE TABLE IF NOT EXISTS file_management.statuses (
    id SERIAL PRIMARY KEY,
    type VARCHAR(25) NOT NULL,
    description VARCHAR(100) NOT NULL,
    update_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    creation_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE file_management.files
ADD COLUMN status_id BIGINT NOT NULL;

ALTER TABLE file_management.files
ADD CONSTRAINT fk_file_status_id
    FOREIGN KEY (status_id)
    REFERENCES file_management.statuses(id);