# PaySetu REST API Documentation

Base URL: `https://api.paysetu.in/v1` (Production) / `https://sandbox-api.paysetu.in/v1` (Sandbox)

All requests require authentication via the `X-Api-Key` header.
All incoming webhooks include an `X-PaySetu-Signature` header calculated using HMAC-SHA256.

---

## 1. Create Payment Order
`POST /v1/orders`

Creates a new payment order for checkout.

### Request Headers
- `X-Api-Key`: string (required)
- `Content-Type`: application/json

### Request Body
```json
{
  "amount": 450.00,
  "currency": "INR",
  "merchantReference": "INV_2026_9012",
  "description": "Sharma Kirana - Weekly Basket",
  "customerPhone": "+919876543210",
  "customerEmail": "customer@example.in"
}
```

### Response (201 Created)
```json
{
  "orderId": "ord_9182310",
  "amount": 450.00,
  "currency": "INR",
  "status": "CREATED",
  "upiDeepLink": "upi://pay?pa=sharmakirana@paysetu&pn=Sharma+Kirana&am=450.00&tr=ord_9182310",
  "expiresAt": 1772659200
}
```

---

## 2. Verify Payment Transaction
`GET /v1/payments/{paymentId}`

Independently queries payment status directly from the server. **Never rely solely on client-side success callbacks.**

### Response (200 OK)
```json
{
  "paymentId": "pay_109283",
  "orderId": "ord_9182310",
  "transactionRef": "428901239841",
  "status": "SUCCESS",
  "amount": 450.00,
  "partnerFee": 0.68,
  "merchantFee": 3.83,
  "platformRevenue": 3.15,
  "taxGst": 0.69,
  "netSettlement": 445.48,
  "timestamp": 1772658300000
}
```

---

## 3. Initiate Refund
`POST /v1/refunds`

Initiates an authorized refund back to the customer's linked UPI account.

### Request Body
```json
{
  "paymentId": "pay_109283",
  "amount": 450.00,
  "reason": "Customer cancelled order before dispatch"
}
```

### Response (200 OK)
```json
{
  "refundId": "rfnd_881923",
  "paymentId": "pay_109283",
  "amount": 450.00,
  "status": "SUCCESS",
  "pspRefundRef": "RRF42891029",
  "timestamp": 1772658400000
}
```
