INSERT INTO orders (created_at, reserved_at, status, sum_price)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '60 minutes', 'RESERVED', 900),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '60 minutes', 'PAID', 500);

INSERT INTO ticket (show_id, seat_id, status)
VALUES
    (1, 1, 'RESERVED'),
    (1, 2, 'PAID');

INSERT INTO order_ticket (order_id, ticket_id)
VALUES
    (1, 1),
    (2, 2);
