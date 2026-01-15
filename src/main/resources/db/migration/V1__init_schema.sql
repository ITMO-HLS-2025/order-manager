CREATE TABLE ticket (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL,
    seat_id BIGINT,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    reserved_at TIMESTAMP,
    status VARCHAR(32) NOT NULL,
    sum_price INT NOT NULL
);

CREATE TABLE order_ticket (
    order_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, ticket_id),
    CONSTRAINT fk_order_ticket_order
        FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_ticket_ticket
        FOREIGN KEY (ticket_id) REFERENCES ticket(id)
);
