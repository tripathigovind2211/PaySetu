package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.DisputeStatus
import com.example.domain.model.KycStatus
import com.example.domain.model.PaymentMode
import com.example.domain.model.RiskLevel
import com.example.domain.model.TransactionState

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val phone: String,
    val fullName: String,
    val email: String,
    val kycStatus: KycStatus = KycStatus.MIN_KYC,
    val maskedPan: String = "",
    val maskedAadhaar: String = "",
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val deviceBindingId: String,
    val biometricEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey val accountId: String,
    val userId: String,
    val bankName: String,
    val ifscCode: String,
    val maskedAccountNumber: String,
    val accountType: String = "SAVINGS",
    val isPrimary: Boolean = false,
    val upiVpa: String,
    val pspBankPartner: String = "HDFC_BANK_PSP",
    val status: String = "ACTIVE"
)

@Entity(tableName = "merchants")
data class MerchantEntity(
    @PrimaryKey val merchantId: String,
    val businessName: String,
    val businessPanMasked: String,
    val gstNumber: String,
    val category: String,
    val vpa: String,
    val apiKey: String,
    val hmacSecret: String,
    val webhookUrl: String,
    val isKycApproved: Boolean = true,
    val settlementCycle: String = "T+1",
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val merchantId: String,
    val merchantReference: String,
    val amount: Double,
    val currency: String = "INR",
    val description: String,
    val customerPhone: String,
    val customerEmail: String,
    val status: String = "CREATED",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (15 * 60 * 1000) // 15 mins expiry
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val orderId: String? = null,
    val transactionRef: String, // Bank RRN / UPI UTR (e.g. 428901239841)
    val payerUserId: String,
    val payerVpa: String,
    val payeeName: String,
    val payeeVpa: String,
    val amount: Double,
    val partnerFee: Double = 0.0,
    val merchantFee: Double = 0.0,
    val platformRevenue: Double = 0.0,
    val taxGst: Double = 0.0,
    val cashback: Double = 0.0,
    val netSettlement: Double,
    val status: TransactionState = TransactionState.CREATED,
    val mode: PaymentMode = PaymentMode.UPI_QR,
    val failureReason: String? = null,
    val riskScore: Int = 10,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val idempotencyKey: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "refunds")
data class RefundEntity(
    @PrimaryKey val refundId: String,
    val paymentId: String,
    val amount: Double,
    val reason: String,
    val status: String = "SUCCESS",
    val pspRefundRef: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val settlementId: String,
    val merchantId: String,
    val grossAmount: Double,
    val totalMdrFee: Double,
    val totalGst: Double,
    val refundsDeducted: Double,
    val netPayableToMerchant: Double,
    val settlementDate: String,
    val status: String = "SETTLED",
    val utrNumber: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "disputes")
data class DisputeEntity(
    @PrimaryKey val disputeId: String,
    val paymentId: String,
    val transactionRef: String,
    val disputeCategory: String, // UNAUTHORIZED, INCORRECT_AMOUNT, MERCHANT_ISSUE
    val status: DisputeStatus = DisputeStatus.OPEN,
    val disputeRefNo: String,
    val userRemarks: String,
    val resolutionRemarks: String? = null,
    val filedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

@Entity(tableName = "fraud_alerts")
data class FraudAlertEntity(
    @PrimaryKey val alertId: String,
    val paymentId: String,
    val triggerRule: String,
    val riskLevel: RiskLevel,
    val actionTaken: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cashback_ledger")
data class CashbackLedgerEntity(
    @PrimaryKey val ledgerId: String,
    val userId: String,
    val paymentId: String,
    val campaignName: String,
    val amount: Double,
    val status: String = "CREDITED",
    val timestamp: Long = System.currentTimeMillis()
)

// Immutable Financial Revenue Ledger
@Entity(tableName = "revenue_ledger")
data class RevenueLedgerEntity(
    @PrimaryKey val ledgerEntryId: String,
    val paymentId: String,
    val transactionAmount: Double,
    val partnerFee: Double,
    val merchantFee: Double,
    val platformRevenue: Double,
    val taxGst: Double,
    val cashbackExpense: Double,
    val netSettlementToMerchant: Double,
    val recordedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val logId: String,
    val actorType: String, // CONSUMER, MERCHANT, ADMIN, SYSTEM
    val actorId: String,
    val action: String,
    val resource: String,
    val ipAddress: String = "192.168.1.10",
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "webhooks")
data class WebhookEntity(
    @PrimaryKey val webhookId: String,
    val merchantId: String,
    val eventType: String, // payment.success, refund.processed, dispute.created
    val payloadJson: String,
    val signatureHmac: String,
    val deliveryStatus: String = "DELIVERED",
    val responseCode: Int = 200,
    val retryCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
