CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.address (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country VARCHAR (40),
    city VARCHAR(80),
    street VARCHAR(100),
    house VARCHAR(40),
    flat VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS delivery.delivery  (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_address_id UUID NOT NULL REFERENCES delivery.address(address_id) ON DELETE CASCADE,
    to_address_id UUID NOT NULL REFERENCES delivery.address(address_id) ON DELETE CASCADE,
    delivery_state VARCHAR (11),
    order_id UUID NOT NULL
);