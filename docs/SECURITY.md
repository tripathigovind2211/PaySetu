# PaySetu Security & Fraud Prevention Architecture

## 1. Zero-Storage Policy
PaySetu adheres to strict non-possession of sensitive authentication credentials:
- **No UPI PIN Storage**: The UPI PIN is entered solely within the encrypted NPCI Common Library (CL) provided by the bank PSP SDK.
- **No OTP Storage**: SMS OTPs are ephemeral and never persisted in database entities.
- **No Banking Passwords / Debit Card PINs / CVV**: Not accepted, processed, or logged anywhere on client or server.

## 2. Real-Time Fraud & Risk Engine
Every payment passes through the multi-stage `FraudRiskEngine`:
- **Transaction Velocity**: Flags accounts attempting >3 payments within 60 seconds.
- **RBI KYC Ceiling**: Automatically blocks payments exceeding ₹10,000 for Min-KYC users.
- **Replay Protection & Idempotency**: Each payment order requires a unique UUID idempotency key; duplicate keys are rejected immediately.
- **Device Fingerprint Binding**: Verifies device binding and SIM authentication before payment initiation.

## 3. Webhook Cryptography
All merchant notifications are signed using **HMAC-SHA256**:
- Header: `X-PaySetu-Signature`
- Payload: Full JSON body of the event
- Merchants compute the signature using their assigned secret key and compare before updating order status.

## 4. Immutable Financial Ledgers
Money movement is recorded using an append-only double-entry ledger (`revenue_ledger`). Historical rows are never edited or deleted directly; refunds and chargebacks create discrete reversing ledger entries.
