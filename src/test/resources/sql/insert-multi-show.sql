INSERT INTO orders (created_at, reserved_at, status, sum_price)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '60 minutes', 'RESERVED', 900);

INSERT INTO ticket (show_id, seat_id, status)
VALUES
    (1, 1, 'RESERVED'),
    (2, 2, 'RESERVED');

INSERT INTO order_ticket (order_id, ticket_id)
VALUES
    (1, 1),
    (1, 2);
