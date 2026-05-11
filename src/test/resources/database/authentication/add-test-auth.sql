INSERT INTO users (id, username, first_name, last_name, email, password, is_deleted)
VALUES
    (1, 'TestUser', 'John', 'Doe', 'user@example.com', 'hashed_password', false),
    (2, 'TestUser2', 'Admin', 'User', 'admin@example.com', 'hashed_password', false);

INSERT INTO roles (id, role)
VALUES
    (1, 'USER'),
    (2, 'ADMIN');

INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2);
