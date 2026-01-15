INSERT INTO ticket (id, show_id, seat_id, status)
VALUES (1, 1, 1, 'RESERVED'),
       (2, 1, 2, 'PAID'),
       (3, 2, 1, 'CANCELLED');

INSERT INTO orders (id, created_at, sum_price, reserved_at, status)
VALUES (1, now(), 1000, now(), 'RESERVED'),
       (2, now(), 1200, null, 'PAID');

INSERT INTO order_ticket (order_id, ticket_id)
VALUES (1, 1),
       (2, 2);
