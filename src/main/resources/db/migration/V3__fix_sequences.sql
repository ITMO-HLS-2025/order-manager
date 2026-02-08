SELECT setval(
    pg_get_serial_sequence('orders', 'id'),
    COALESCE((SELECT MAX(id) FROM orders), 1),
    (SELECT MAX(id) IS NOT NULL FROM orders)
);

SELECT setval(
    pg_get_serial_sequence('ticket', 'id'),
    COALESCE((SELECT MAX(id) FROM ticket), 1),
    (SELECT MAX(id) IS NOT NULL FROM ticket)
);
