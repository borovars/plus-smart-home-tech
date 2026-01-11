CREATE TABLE IF NOT EXISTS address (
    id UUID PRIMARY KEY,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    house VARCHAR(10),
    flat VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS order_delivery (
    id UUID primary key,
    total_volume BIGINT,
    total_weight BIGINT,
    fragile BOOLEAN,
    from_address_id UUID REFERENCES address(id) NOT NULL,
    to_address_id UUID REFERENCES address(id) NOT NULL,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(50)
);