-- Legacy records remain unlinked. Never infer identity from matching IDs/emails.
ALTER TABLE users
    ADD COLUMN auth_user_id BIGINT NULL,
    ADD CONSTRAINT uk_users_auth_user_id UNIQUE (auth_user_id),
    DROP COLUMN password;
