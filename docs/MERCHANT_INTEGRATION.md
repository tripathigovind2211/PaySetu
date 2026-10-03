# PaySetu Merchant Integration Guide

## 1. Integration Steps

### Step 1: Create an Order on your Server
Never initiate payment from the frontend without a server-backed order.

```bash
curl -X POST https://api.paysetu.in/v1/orders \
  -H "X-Api-Key: ps_live_your_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 450.00,
    "currency": "INR",
    "merchantReference": "ORDER_109283",
    "description": "Grocery Items"
  }'
```

Response:
```json
{
  "orderId": "ord_9182310",
  "amount": 450.00,
  "status": "CREATED",
  "upiDeepLink": "upi://pay?pa=merchant@paysetu&pn=Merchant+Store&am=450.00&tr=ord_9182310"
}
```

### Step 2: Open PaySetu Checkout in App
Pass the `upiDeepLink` or use the native Android / iOS SDK to launch the payment sheet.

### Step 3: Receive & Verify Webhook
Listen for `payment.success` on your webhook endpoint and verify the `X-PaySetu-Signature` header.

### Step 4: Verify Independently via REST API
Always verify status directly before dispensing goods:

```bash
curl -X GET https://api.paysetu.in/v1/payments/ord_9182310 \
  -H "X-Api-Key: ps_live_your_key_here"
```
