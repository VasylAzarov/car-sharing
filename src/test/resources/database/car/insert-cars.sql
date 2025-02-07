ALTER TABLE cars AUTO_INCREMENT = 1;
INSERT INTO cars (model, brand, type, inventory, daily_fee, is_deleted)
VALUES ('Model S', 'Tesla', 'SEDAN', 5, 99.99, false),
       ('X5', 'BMW', 'SUV', 3, 129.99, false),
       ('Golf', 'Volkswagen', 'HATCHBACK', 10, 59.99, false);