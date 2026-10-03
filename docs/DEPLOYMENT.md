# PaySetu Production Deployment & Environment Guide

## 1. Environments

PaySetu enforces strict environment separation:
1. **DEMO MODE**: Self-contained interactive sandbox for rapid prototyping, user acceptance testing, and pre-sales demonstration.
2. **SANDBOX MODE**: Calls test API switches with mock banks (HDFC, SBI, ICICI) and validates HMAC signatures.
3. **PRODUCTION MODE**: Strict enforcement of live acquiring bank credentials, NPCI Common Library HSM keys, and RBI Payment Aggregator approvals.

---

## 2. Infrastructure Setup

```bash
# Start PostgreSQL 15, Redis 7, and PaySetu Payment Gateway
docker-compose up -d

# Verify Database Schemas and Seed Data
docker-compose exec postgres psql -U paysetu_admin -d paysetu_production -f /docker-entrypoint-initdb.d/schema.sql
```

---

## 3. High Availability & Disaster Recovery
- **Database**: Active-Passive PostgreSQL cluster with synchronous replication for zero data loss on financial transactions.
- **Cache**: Redis sentinel cluster for distributed rate limiting and idempotency tracking.
- **Ledger Invariance**: Automated hourly checksum jobs comparing transaction debits against net settlement payouts.
