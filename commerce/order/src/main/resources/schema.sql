CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE IF NOT EXISTS orders.orders  (
    order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopping_cart_id UUID NOT NULL,
    payment_id UUID,
    delivery_id UUID,
    order_state VARCHAR (10) NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    totalPrice DOUBLE PRECISION,
    delivery_price DOUBLE PRECISION,
    productPrice DOUBLE PRECISION
    );

CREATE TABLE IF NOT EXISTS orders.order_products
(
    order_id   UUID NOT NULL REFERENCES orders.orders (order_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity   INT  NOT NULL,
    PRIMARY KEY (order_id, product_id)
);
