CREATE SCHEMA IF NOT EXISTS shopping_store;

CREATE TABLE IF NOT EXISTS shopping_store.store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(40) NOT NULL,
    description VARCHAR(200) NOT NULL,
    image_src VARCHAR(200),
    quantity_state VARCHAR(40) NOT NULL,
    product_state VARCHAR(40) NOT NULL,
    product_category VARCHAR(40),
    price DOUBLE PRECISION NOT NULL CHECK (price >= 1)
    );