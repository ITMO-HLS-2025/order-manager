ALTER TABLE orders
    ADD COLUMN user_id BIGINT;

UPDATE orders
SET user_id = 1
WHERE user_id IS NULL;

ALTER TABLE orders
    ALTER COLUMN user_id SET NOT NULL;
