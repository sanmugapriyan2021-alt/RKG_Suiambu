-- ==========================================================================
-- RKG SUYAMBU POS BILLING SYSTEM - OFFLINE SQL RECONCILIATION DATABASE SCHEMA
-- ==========================================================================

CREATE TABLE IF NOT EXISTS offline_bills (
    bill_number VARCHAR(64) PRIMARY KEY,
    invoice_connected VARCHAR(64),
    transaction_date VARCHAR(20),
    date_time VARCHAR(30),
    customer_name VARCHAR(120),
    customer_mobile_number VARCHAR(20),
    product_purchased_by VARCHAR(50),
    payment_mode VARCHAR(50),
    subtotal DECIMAL(10,2),
    discount DECIMAL(10,2),
    total_amount_of_the_bill DECIMAL(10,2),
    promo_code_used VARCHAR(50),
    sync_status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS offline_daily_transactions (
    transaction_id VARCHAR(64) PRIMARY KEY,
    bill_number VARCHAR(64),
    transaction_date VARCHAR(20),
    customer_name VARCHAR(120),
    total_amount DECIMAL(10,2),
    payment_mode VARCHAR(50),
    billed_by VARCHAR(50),
    sync_status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS offline_users (
    username VARCHAR(50) PRIMARY KEY,
    role VARCHAR(50),
    password_hash VARCHAR(100),
    status VARCHAR(20),
    last_login VARCHAR(30)
);

-- Pre-seed billing operator credentials
INSERT OR REPLACE INTO offline_users (username, role, password_hash, status, last_login)
VALUES ('billing', 'Billing', '230826', 'Active', datetime('now'));
