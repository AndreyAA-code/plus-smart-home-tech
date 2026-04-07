CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.delivery  (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid()
    );