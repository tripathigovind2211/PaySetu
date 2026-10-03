-- ============================================================================
-- PaySetu Payment Gateway & UPI PSP Bridge - Production PostgreSQL Schema
-- Compliant with RBI Master Directions, NPCI UPI Specs, & DPDP Act 2023
-- ZERO STORAGE: UPI PIN, CVV, OTP, Banking passwords are strictly prohibited.
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Users (Masked Identity under DPDP Act)
CREATE TABLE users (
    user_id VARCHAR(64) PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    kyc_status VARCHAR(32) NOT NULL DEFAULT 'MIN_KYC', -- MIN_KYC, FULL_KYC, REJECTED
    masked_pan VARCHAR(16),
    masked_aadhaar VARCHAR(24),
    risk_level VARCHAR(16) NOT NULL DEFAULT 'LOW', -- LOW, MEDIUM, HIGH, BLOCKED
    device_binding_id VARCHAR(128) NOT NULL,
    biometric_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bank Accounts & Linked UPI Payment Methods
CREATE TABLE bank_accounts (
    account_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) REFERENCES users(user_id) ON DELETE CASCADE,
    bank_name VARCHAR(128) NOT NULL,
    ifsc_code VARCHAR(16) NOT NULL,
    masked_account_number VARCHAR(32) NOT NULL,
    account_type VARCHAR(32) NOT NULL DEFAULT 'SAVINGS',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    upi_vpa VARCHAR(128) NOT NULL UNIQUE,
    psp_bank_partner VARCHAR(64) NOT NULL DEFAULT 'HDFC_BANK_PSP',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Merchants
CREATE TABLE merchants (
    merchant_id VARCHAR(64) PRIMARY KEY,
    business_name VARCHAR(256) NOT NULL,
    business_pan_masked VARCHAR(16) NOT NULL,
    gst_number VARCHAR(24) NOT NULL,
    category VARCHAR(128) NOT NULL,
    vpa VARCHAR(128) NOT NULL,
    api_key_hash VARCHAR(128) NOT NULL,
    hmac_secret VARCHAR(128) NOT NULL,
    webhook_url TEXT NOT NULL,
    is_kyc_approved BOOLEAN NOT NULL DEFAULT FALSE,
    settlement_cycle VARCHAR(16) NOT NULL DEFAULT 'T+1',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Orders
CREATE TABLE orders (
    order_id VARCHAR(64) PRIMARY KEY,
    merchant_id VARCHAR(64) REFERENCES merchants(merchant_id),
    merchant_reference VARCHAR(128) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(8) NOT NULL DEFAULT 'INR',
    description TEXT,
    customer_phone VARCHAR(20),
    customer_email VARCHAR(128),
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED', -- CREATED, PAID, EXPIRED, CANCELLED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 5. Payments
CREATE TABLE payments (
    payment_id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) REFERENCES orders(order_id),
    transaction_ref VARCHAR(32) NOT NULL UNIQUE, -- 12-digit Indian UPI UTR / RRN
    payer_user_id VARCHAR(64) REFERENCES users(user_id),
    payer_vpa VARCHAR(128) NOT NULL,
    payee_name VARCHAR(256) NOT NULL,
    payee_vpa VARCHAR(128) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    partner_fee NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    merchant_fee NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    platform_revenue NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    tax_gst NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    cashback NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    net_settlement NUMERIC(12, 2) NOT NULL,
    status VARCHAR(32) NOT NULL, -- CREATED, INITIATED, PENDING, SUCCESS, FAILED, REFUNDED, DISPUTED
    mode VARCHAR(32) NOT NULL, -- UPI_QR, UPI_INTENT, VPA_DIRECT, ONLINE_CHECKOUT
    failure_reason TEXT,
    risk_score INT NOT NULL DEFAULT 10,
    risk_level VARCHAR(16) NOT NULL DEFAULT 'LOW',
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Immutable Financial Revenue Ledger (Append-only)
CREATE TABLE revenue_ledger (
    ledger_entry_id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) REFERENCES payments(payment_id),
    transaction_amount NUMERIC(12, 2) NOT NULL,
    partner_fee NUMERIC(12, 2) NOT NULL,
    merchant_fee NUMERIC(12, 2) NOT NULL,
    platform_revenue NUMERIC(12, 2) NOT NULL,
    tax_gst NUMERIC(12, 2) NOT NULL,
    cashback_expense NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    net_settlement_to_merchant NUMERIC(12, 2) NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Refunds
CREATE TABLE refunds (
    refund_id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) REFERENCES payments(payment_id),
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    reason TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
    psp_refund_ref VARCHAR(64) NOT NULL UNIQUE,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Settlements (T+1 Escrow Batches)
CREATE TABLE settlements (
    settlement_id VARCHAR(64) PRIMARY KEY,
    merchant_id VARCHAR(64) REFERENCES merchants(merchant_id),
    gross_amount NUMERIC(14, 2) NOT NULL,
    total_mdr_fee NUMERIC(14, 2) NOT NULL,
    total_gst NUMERIC(14, 2) NOT NULL,
    refunds_deducted NUMERIC(14, 2) NOT NULL DEFAULT 0.00,
    net_payable_to_merchant NUMERIC(14, 2) NOT NULL,
    settlement_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'SETTLED',
    utr_number VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. Disputes & NPCI ODR
CREATE TABLE disputes (
    dispute_id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) REFERENCES payments(payment_id),
    transaction_ref VARCHAR(32) NOT NULL,
    dispute_category VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    dispute_ref_no VARCHAR(64) NOT NULL UNIQUE,
    user_remarks TEXT NOT NULL,
    resolution_remarks TEXT,
    filed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE
);

-- 10. Fraud & Risk Alerts
CREATE TABLE fraud_alerts (
    alert_id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64),
    trigger_rule VARCHAR(128) NOT NULL,
    risk_level VARCHAR(16) NOT NULL,
    action_taken VARCHAR(64) NOT NULL,
    details TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 11. Security Audit Logs (Immutable)
CREATE TABLE audit_logs (
    log_id VARCHAR(64) PRIMARY KEY,
    actor_type VARCHAR(32) NOT NULL,
    actor_id VARCHAR(64) NOT NULL,
    action VARCHAR(128) NOT NULL,
    resource VARCHAR(256) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    details TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for high-throughput query performance
CREATE INDEX idx_payments_payer ON payments(payer_user_id);
CREATE INDEX idx_payments_utr ON payments(transaction_ref);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_revenue_ledger_payment ON revenue_ledger(payment_id);
CREATE INDEX idx_orders_merchant ON orders(merchant_id);
