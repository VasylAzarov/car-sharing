ALTER TABLE users AUTO_INCREMENT = 1;
INSERT INTO users (email, first_name, last_name, password, is_deleted)
VALUES
    ('manager@example.com', 'John', 'Doe', 'securepass', false),
    ('customer1@example.com', 'Jane', 'Smith', 'securepass', false),
    ('customer2@example.com', 'Bob', 'Brown', 'securepass', false);

INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 2),
    (2, 1),
    (2, 1);