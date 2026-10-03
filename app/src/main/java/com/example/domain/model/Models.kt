package com.example.domain.model

enum class TransactionState {
    CREATED,
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    EXPIRED,
    REFUND_INITIATED,
    REFUNDED,
    DISPUTED,
    CANCELLED
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    BLOCKED
}

enum class EnvironmentMode {
    DEMO,
    SANDBOX,
    PRODUCTION
}

enum class KycStatus {
    MIN_KYC,
    FULL_KYC,
    PENDING,
    REJECTED
}

enum class PaymentMode {
    UPI_QR,
    UPI_INTENT,
    VPA_DIRECT,
    ONLINE_CHECKOUT
}

enum class DisputeStatus {
    OPEN,
    UNDER_INVESTIGATION,
    RESOLVED_REFUND,
    REJECTED
}

enum class UserRole {
    CONSUMER,
    MERCHANT,
    ADMIN,
    DEVELOPER
}

data class RevenueConfiguration(
    val partnerFeePercent: Double = 0.15, // 0.15% to partner PSP bank
    val merchantFeePercent: Double = 0.85, // 0.85% MDR charged to merchant
    val platformRevenuePercent: Double = 0.70, // Net margin (0.85 - 0.15)
    val gstRatePercent: Double = 18.0, // 18% GST on platform services
    val cashbackRatePercent: Double = 1.0, // Promotional cashback when applicable
    val maxCashbackAmount: Double = 50.0
)

data class RevenueCalculation(
    val transactionAmount: Double,
    val partnerFee: Double,
    val merchantFee: Double,
    val platformRevenue: Double,
    val taxGst: Double,
    val cashback: Double,
    val refundAmount: Double = 0.0,
    val netSettlement: Double,
    val payerConvenienceFee: Double = 0.0, // Transparently 0 for standard UPI
    val finalPayableAmount: Double
)

data class UpiQrPayload(
    val payeeVpa: String,
    val payeeName: String,
    val amount: Double? = null,
    val transactionNote: String? = null,
    val transactionRef: String? = null,
    val merchantCategoryCode: String? = null,
    val currency: String = "INR",
    val url: String? = null
)

data class FraudEvaluationResult(
    val riskLevel: RiskLevel,
    val riskScore: Int, // 0 - 100
    val triggeredRules: List<String>,
    val recommendation: String,
    val allowProcessing: Boolean
)

data class ReconciliationSummary(
    val totalTransactions: Int,
    val matchedTransactions: Int,
    val amountMismatches: Int,
    val missingInternal: Int,
    val missingProvider: Int,
    val delayedSettlements: Int,
    val totalReconciledVolume: Double,
    val discrepancyAmount: Double,
    val lastRunTimestamp: Long
)

data class ComplianceRequirement(
    val id: String,
    val authority: String, // RBI, NPCI, DPDP, PCI-DSS, GST
    val title: String,
    val description: String,
    val isMandatory: Boolean,
    val status: ComplianceStatus,
    val auditNote: String
)

enum class ComplianceStatus {
    COMPLIANT,
    IN_PROGRESS,
    NOT_PRODUCTION_READY,
    PENDING_AUDIT
}
