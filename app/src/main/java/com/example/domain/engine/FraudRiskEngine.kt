package com.example.domain.engine

import com.example.domain.model.FraudEvaluationResult
import com.example.domain.model.KycStatus
import com.example.domain.model.RiskLevel

class FraudRiskEngine {

    /**
     * Evaluates transaction parameters against RBI/NPCI risk norms.
     */
    fun evaluateTransaction(
        amount: Double,
        kycStatus: KycStatus,
        recentTxCountLastHour: Int,
        recentFailedCount: Int,
        isDeviceBindingValid: Boolean,
        isDuplicateIdempotency: Boolean,
        isAbnormalHour: Boolean = false
    ): FraudEvaluationResult {
        val triggeredRules = mutableListOf<String>()
        var score = 5 // Base clean score

        // 1. Replay / Duplicate Idempotency Key check
        if (isDuplicateIdempotency) {
            triggeredRules.add("REPLAY_ATTEMPT_DUPLICATE_IDEMPOTENCY_KEY")
            return FraudEvaluationResult(
                riskLevel = RiskLevel.BLOCKED,
                riskScore = 100,
                triggeredRules = triggeredRules,
                recommendation = "Reject immediately. Possible replay attack or double debit attempt.",
                allowProcessing = false
            )
        }

        // 2. Device Binding / SIM verification check (NPCI UPI security guideline)
        if (!isDeviceBindingValid) {
            triggeredRules.add("DEVICE_BINDING_MISMATCH")
            return FraudEvaluationResult(
                riskLevel = RiskLevel.BLOCKED,
                riskScore = 95,
                triggeredRules = triggeredRules,
                recommendation = "Device binding fingerprint does not match authorized SIM. Require re-binding.",
                allowProcessing = false
            )
        }

        // 3. KYC Limits as per RBI Master Directions
        if (kycStatus == KycStatus.MIN_KYC && amount > 10000.0) {
            triggeredRules.add("RBI_MIN_KYC_TRANSACTION_LIMIT_EXCEEDED")
            return FraudEvaluationResult(
                riskLevel = RiskLevel.BLOCKED,
                riskScore = 90,
                triggeredRules = triggeredRules,
                recommendation = "Transaction amount ₹$amount exceeds Min-KYC limit of ₹10,000. Complete Full Video-KYC to proceed.",
                allowProcessing = false
            )
        }

        // 4. Single Transaction High Value Check
        if (amount > 100000.0) {
            score += 35
            triggeredRules.add("HIGH_VALUE_TRANSACTION_THRESHOLD")
        }

        // 5. Velocity Checks
        if (recentTxCountLastHour >= 5) {
            score += 40
            triggeredRules.add("UNUSUAL_TRANSACTION_VELOCITY_5_PLUS_PER_HOUR")
        } else if (recentTxCountLastHour >= 3) {
            score += 20
            triggeredRules.add("ELEVATED_VELOCITY_3_PER_HOUR")
        }

        // 6. Repeated Failures (Brute force / PIN probing detection)
        if (recentFailedCount >= 3) {
            score += 35
            triggeredRules.add("REPEATED_FAILED_PAYMENT_STREAK")
        }

        // 7. Abnormal Hour Check (2 AM - 5 AM high value)
        if (isAbnormalHour && amount > 25000.0) {
            score += 15
            triggeredRules.add("UNUSUAL_OFF_PEAK_HIGH_VALUE_ACTIVITY")
        }

        val riskLevel = when {
            score >= 75 -> RiskLevel.BLOCKED
            score >= 50 -> RiskLevel.HIGH
            score >= 25 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val recommendation = when (riskLevel) {
            RiskLevel.BLOCKED -> "Transaction blocked by risk policy. Requires manual compliance unfreeze."
            RiskLevel.HIGH -> "High risk transaction. Step-up multi-factor biometric authentication required."
            RiskLevel.MEDIUM -> "Medium risk detected. Flagged for async automated post-transaction review."
            RiskLevel.LOW -> "Transaction cleared risk checks. Proceed to payment switch."
        }

        return FraudEvaluationResult(
            riskLevel = riskLevel,
            riskScore = score.coerceIn(0, 100),
            triggeredRules = triggeredRules,
            recommendation = recommendation,
            allowProcessing = riskLevel != RiskLevel.BLOCKED
        )
    }
}
