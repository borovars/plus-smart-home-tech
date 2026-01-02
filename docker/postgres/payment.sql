CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    products_total DECIMAL,
    delivery_total DECIMAL,
    total_payment  DECIMAL,
    fee_total DECIMAL,
    payment_state VARCHAR(50)
);