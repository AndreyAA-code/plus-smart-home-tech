CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payment  (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid()
    );