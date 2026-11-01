INSERT INTO users (username, password, email, first_name, last_name, is_deleted)
VALUES ('TestUser', 'hashed_password', 'testuser@example.com', 'John', 'Doe', false),
       ('TestAdmin', 'hashed_password', 'admin@example.com', 'Admin', 'User', false);

INSERT INTO users_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE role = 'USER';

INSERT INTO users_roles (user_id, role_id)
SELECT 2, id FROM roles WHERE role = 'ADMIN';

INSERT INTO projects (name, description, start_date, end_date, status, user_id)
VALUES ('Test Project', 'This is a test project', '2025-03-11', '2025-03-12', 'INITIATED', 1);

INSERT INTO tasks (name, description, due_date, project_id, assignee_id)
VALUES ('Task for Testing', 'Task description', '2025-03-15', 1, 1);

INSERT INTO comments (content, text, task_id, user_id)
VALUES ('This is the first comment.', 'first text', 1, 1),
       ('This is the second comment.', 'second text', 1, 2);

INSERT INTO attachments (filename, dropbox_file_id, task_id)
VALUES ('test-file.txt', 'dropbox-id-1', 1);

INSERT INTO labels (name, color)
VALUES ('Important', 'white');