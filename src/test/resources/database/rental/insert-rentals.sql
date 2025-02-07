ALTER TABLE rentals AUTO_INCREMENT = 1;
INSERT INTO rentals (rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES
    ('2024-01-01', '2024-01-10', NULL, 1, 2, false),
    ('2024-02-01', '2024-02-05', '2024-02-05', 2, 3, false),
    ('2024-03-01', '2024-03-07', NULL, 3, 3, false);