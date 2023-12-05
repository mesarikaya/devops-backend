--Drop schema if exists
DROP SCHEMA IF EXISTS file_management cascade;

-- CREATE SCHEMA
CREATE SCHEMA IF NOT EXISTS file_management;

CREATE TABLE IF NOT EXISTS file_management.files (
    id SERIAL PRIMARY KEY,
    uuid UUID DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    update_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    creation_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    storage_path VARCHAR(255),
    metadata JSONB,
    visibility VARCHAR(100)
);