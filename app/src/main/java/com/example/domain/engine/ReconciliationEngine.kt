package com.example.domain.engine

import com.example.data.local.entities.PaymentEntity
import com.example.domain.model.ReconciliationSummary
import com.example.domain.model.TransactionState

data class ExternalPspRecord(
    val transactionRef: String,
    val amount: Double,
    val status: String,
    val bankRefNo: String,
    val settlementTimestamp: Long
)

data class DiscrepancyRecord(
    val transactionRef: String,
    val type: DiscrepancyType,
    val internalAmount: Double?,
    val externalAmount: Double?,
    val details: String
)

enum class DiscrepancyType {
    AMOUNT_MISMATCH,
    MISSING_IN_INTERNAL_LEDGER,
    MISSING_IN_PSP_RECORDS,
    STATUS_MISMATCH,
    DELAYED_SETTLEMENT
}

class ReconciliationEngine {

    fun performThreeWayReconciliation(
        internalPayments: List<PaymentEntity>,
        externalPspRecords: List<ExternalPspRecord>
    ): Pair<ReconciliationSummary, List<DiscrepancyRecord>> {
        val discrepancies = mutableListOf<DiscrepancyRecord>()
        val pspMap = externalPspRecords.associateBy { it.transactionRef }
        val internalMap = internalPayments.associateBy { it.transactionRef }

        var matchedCount = 0
        var amountMismatchCount = 0
        var missingInInternal = 0
        var missingInPsp = 0
        var delayedSettlements = 0
        var totalReconciledVolume = 0.0
        var discrepancyTotalAmount = 0.0

        // 1. Check each internal record against PSP
        for (payment in internalPayments) {
            val pspRecord = pspMap[payment.transactionRef]
            if (pspRecord == null) {
                if (payment.status == TransactionState.SUCCESS) {
                    missingInPsp++
                    discrepancyTotalAmount += payment.amount
                    discrepancies.add(
                        DiscrepancyRecord(
                            transactionRef = payment.transactionRef,
                            type = DiscrepancyType.MISSING_IN_PSP_RECORDS,
                            internalAmount = payment.amount,
                            externalAmount = null,
                            details = "Internal status is SUCCESS but no corresponding PSP record found."
                        )
                    )
                }
            } else {
                // Check Amount
                if (Math.abs(payment.amount - pspRecord.amount) > 0.01) {
                    amountMismatchCount++
                    val diff = Math.abs(payment.amount - pspRecord.amount)
                    discrepancyTotalAmount += diff
                    discrepancies.add(
                        DiscrepancyRecord(
                            transactionRef = payment.transactionRef,
                            type = DiscrepancyType.AMOUNT_MISMATCH,
                            internalAmount = payment.amount,
                            externalAmount = pspRecord.amount,
                            details = "Internal amount ₹${payment.amount} != PSP amount ₹${pspRecord.amount}"
                        )
                    )
                } else if (payment.status == TransactionState.SUCCESS && pspRecord.status == "SUCCESS") {
                    matchedCount++
                    totalReconciledVolume += payment.amount
                } else if (payment.status == TransactionState.SUCCESS && pspRecord.status != "SUCCESS") {
                    discrepancies.add(
                        DiscrepancyRecord(
                            transactionRef = payment.transactionRef,
                            type = DiscrepancyType.STATUS_MISMATCH,
                            internalAmount = payment.amount,
                            externalAmount = pspRecord.amount,
                            details = "Status mismatch: Internal is SUCCESS, PSP is ${pspRecord.status}"
                        )
                    )
                }
            }
        }

        // 2. Check PSP records missing internally
        for (psp in externalPspRecords) {
            if (!internalMap.containsKey(psp.transactionRef)) {
                missingInInternal++
                discrepancyTotalAmount += psp.amount
                discrepancies.add(
                    DiscrepancyRecord(
                        transactionRef = psp.transactionRef,
                        type = DiscrepancyType.MISSING_IN_INTERNAL_LEDGER,
                        internalAmount = null,
                        externalAmount = psp.amount,
                        details = "Payment exists on PSP gateway but missing in local ledger (dropped webhook)."
                    )
                )
            }
        }

        val summary = ReconciliationSummary(
            totalTransactions = internalPayments.size,
            matchedTransactions = matchedCount,
            amountMismatches = amountMismatchCount,
            missingInternal = missingInInternal,
            missingProvider = missingInPsp,
            delayedSettlements = delayedSettlements,
            totalReconciledVolume = totalReconciledVolume,
            discrepancyAmount = discrepancyTotalAmount,
            lastRunTimestamp = System.currentTimeMillis()
        )

        return Pair(summary, discrepancies)
    }
}
