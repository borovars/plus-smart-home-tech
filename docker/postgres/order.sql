CREATE TABLE IF NOT EXISTS address (
    id UUID PRIMARY KEY,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    house VARCHAR(10),
    flat VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255),
    order_state VARCHAR(50),
    shopping_cart_id UUID,
    delivery_id UUID,
    delivery_address_id UUID REFERENCES address(id),
    payment_id UUID,
    delivery_volume DECIMAL,
    delivery_weight DECIMAL,
    fragile BOOLEAN,
    total_price DECIMAL,
    product_price DECIMAL,
    delivery_price DECIMAL
);

CREATE TABLE IF NOT EXISTS order_products (
    product_id UUID,
    order_id UUID REFERENCES orders(id),
    quantity BIGINT,
    PRIMARY KEY (product_id, order_id)
);