INSERT INTO users (id, username, password, email, is_deleted)
VALUES (1, 'TestUser', 'password123', 'testuser@example.com', false),
       (2, 'Test2User', 'password1223', 'testuse2r@example.com', false);

INSERT INTO projects (id, name, description, start_date, end_date, status, user_id)
VALUES (2, 'Test Project', 'This is a test project', '2025-03-11', '2025-03-12', 'INITIATED', 1);

INSERT INTO tasks (id, assignee_id, description, due_date, name, project_id)
VALUES (1, 1, 'Task description', '2025-03-15', 'Task for Testing', 2);
