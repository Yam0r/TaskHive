ALTER TABLE users AUTO_INCREMENT = 1;

INSERT INTO users (username, password, email, is_deleted)
VALUES
    ('TestUser', 'password123', 'testuser@example.com', false),
    ('Test2User', 'password1223', 'testuse2r@example.com', false);

INSERT INTO projects (id, name, description, start_date, end_date, status, user_id)
VALUES
    (2, 'Test Project', 'This is a test project', '2025-03-11', '2025-03-12', 'INITIATED', 1);

INSERT INTO tasks (id, assignee_id, description, due_date, name, project_id)
VALUES
    (1, 1, 'Task description', '2025-03-15', 'Task for Testing', 2);

INSERT INTO comments (id, content, text, task_id, user_id)
VALUES
    (1, 'First comment', 'first text', 1, 1),
    (2, 'Second comment', 'second text', 1, 2);

INSERT INTO labels (name, color)
VALUES
    ('Important', 'white');
