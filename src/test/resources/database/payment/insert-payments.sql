ALTER TABLE payments AUTO_INCREMENT = 1;
INSERT INTO payments (status, type, rental_id, session_url, session_id, amount_to_pay, is_deleted)
VALUES
    ('PENDING', 'PAYMENT', 1, 'https://payment-gateway.com/session1', 'sess_12345', 99.99, false),
    ('PAID', 'PAYMENT', 2, 'https://payment-gateway.com/session2', 'sess_67890', 129.99, false),
    ('PENDING', 'FINE', 3, 'https://payment-gateway.com/session3', 'sess_54321', 59.99, false);