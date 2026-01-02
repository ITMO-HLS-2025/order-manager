CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    reserved_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    sum_price INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ticket (
    id SERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL,
    seat_id BIGINT,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_ticket (
    order_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, ticket_id),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE
);
