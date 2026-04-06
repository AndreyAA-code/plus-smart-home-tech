CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE IF NOT EXISTS orders.orders  (
    order_id UUID PRIMARY KEY DEFAULT gen_random_uuid()
    );