DELETE FROM users_roles;
DELETE FROM tasks;
DELETE FROM projects;
DELETE FROM users;
DELETE FROM roles;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE roles AUTO_INCREMENT = 1;
