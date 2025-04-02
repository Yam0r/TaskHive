INSERT INTO users (id, username, password, email, is_deleted)
VALUES
    (1, 'TestUser', 'password123', 'testuser@example.com', false),
    (2, 'Test2User', 'password1223', 'testuse2r@example.com', false);

INSERT INTO projects (id, name, description, start_date, end_date, status, user_id)
VALUES
    (2, 'Test Project', 'This is a test project', '2025-03-11', '2025-03-12', 'INITIATED', 1);

INSERT INTO tasks (id, assignee_id, description, due_date, name, project_id)
VALUES
    (1, 1, 'Task description', '2025-03-15', 'Task for Testing', 2);

INSERT INTO comments (id, content, text, task_id, user_id)
VALUES
    (1, 'This is the first comment.', 'first text', 1, 1),
    (2, 'This is the second comment.', 'second text', 1, 2)
    ON DUPLICATE KEY UPDATE content = VALUES(content);

INSERT INTO attachments (id, filename, dropbox_file_id, task_id)
VALUES
    (1, 'test-file.txt', 'dropbox-id-1', 1);
