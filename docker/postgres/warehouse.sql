CREATE TABLE IF NOT EXISTS warehouse_products(
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    depth DOUBLE PRECISION NOT NULL,
    weight DOUBLE PRECISION,
    quantity BIGINT
);

CREATE TABLE IF NOT EXISTS order_booking (
    order_id UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS order_booking_products (
    order_booking_id UUID REFERENCES order_booking(order_id),
    product_id uuid NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0
);