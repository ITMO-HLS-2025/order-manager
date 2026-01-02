INSERT INTO orders (created_at, reserved_at, status, sum_price)
VALUES
    (NOW(), NOW() + INTERVAL '60 minutes', 'RESERVED', 900),
    ( NOW(), NOW() + INTERVAL '60 minutes', 'PAID', 500);
SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT MAX(id) FROM orders));

INSERT INTO ticket (show_id, seat_id, status)
VALUES
    ( 1, 1, 'RESERVED'),
    ( 1, 2, 'PAID');
SELECT setval(pg_get_serial_sequence('ticket', 'id'), (SELECT MAX(id) FROM ticket));

INSERT INTO order_ticket (order_id, ticket_id)
VALUES
    (1, 1),
    (2, 2);
