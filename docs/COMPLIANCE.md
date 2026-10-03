# PaySetu Compliance & Regulatory Readiness Checklist

## 1. Overview
PaySetu is engineered strictly within the bounds of Indian financial regulations. It is designed to work as an authorized Third-Party Application Provider (TPAP) and Payment Aggregator (PA) bridge in partnership with scheduled commercial banks (such as HDFC Bank, ICICI Bank, and State Bank of India).

---

## 2. Regulatory Matrix

| Regulatory Authority | Regulation / Act | Status | Architecture Control |
|---|---|---|---|
| **Reserve Bank of India (RBI)** | Payment and Settlement Systems Act, 2007 (PSSA) | In-Principle Partner Integration | Escrow accounts routed through scheduled commercial bank partners. |
| **RBI Master Directions** | Master Direction on Prepaid Payment Instruments (PPI) | Enforced | Tiered KYC: Min-KYC limit capped at ₹10,000/month; Full Video-KYC (V-CIP) for higher limits. |
| **NPCI** | UPI Procedural Guidelines & Technical Specifications | Enforced | Direct delegation to authorized Bank PSP Common Library (CL); standard `upi://pay` deep linking. |
| **RBI Cyber Security** | Storage of Payment System Data Directives | Strict Zero-Storage | Absolutely zero storage of UPI PIN, card CVV, net-banking credentials, or OTPs. |
| **DPDP Act, 2023** | Digital Personal Data Protection Act | Enforced | Masked PAN/Aadhaar storage; granular consent logs for device fingerprint binding. |
| **CBIC / GST** | Central Goods and Services Tax Act | Enforced | 18% statutory GST levied on merchant MDR fees (SAC Code 997159) with automated reporting. |
| **NPCI / RBI Ombudsman** | Online Dispute Resolution (ODR) Framework | Enforced | In-app dispute filing with automated tracking reference numbers. |

---

## 3. Production Readiness Declaration

> **CRITICAL COMPLIANCE NOTICE**:
> If the live partner acquiring bank sponsorship, NPCI TPAP certification, or RBI Payment Aggregator in-principle license is absent, PaySetu operates strictly in **DEMO** or **SANDBOX MODE**. It will never execute live financial transactions or present simulated test success as live banking settlements without the requisite statutory licenses.
