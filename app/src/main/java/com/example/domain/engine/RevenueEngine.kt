package com.example.domain.engine

import com.example.domain.model.RevenueCalculation
import com.example.domain.model.RevenueConfiguration
import java.math.BigDecimal
import java.math.RoundingMode

class RevenueEngine(
    private var config: RevenueConfiguration = RevenueConfiguration()
) {
    fun getCurrentConfig(): RevenueConfiguration = config

    fun updateConfig(newConfig: RevenueConfiguration) {
        config = newConfig
    }

    /**
     * Computes the compliant, itemized financial breakdown with zero hidden charges.
     * All monetary calculations use 2 decimal places with HALF_UP rounding.
     */
    fun calculate(
        transactionAmount: Double,
        isCashbackEligible: Boolean = false,
        refundAmount: Double = 0.0
    ): RevenueCalculation {
        val amount = BigDecimal(transactionAmount.toString()).setScale(2, RoundingMode.HALF_UP)
        val refund = BigDecimal(refundAmount.toString()).setScale(2, RoundingMode.HALF_UP)

        // 1. Merchant Fee (MDR legally agreed under merchant agreement)
        val merchantFee = amount.multiply(BigDecimal(config.merchantFeePercent.toString()))
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        // 2. Partner PSP Bank Fee (interchange / processing fee paid to acquiring bank)
        val partnerFee = amount.multiply(BigDecimal(config.partnerFeePercent.toString()))
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        // 3. Platform Revenue = Merchant Fee - Partner Fee
        val platformRevenue = merchantFee.subtract(partnerFee).max(BigDecimal.ZERO)

        // 4. Statutory Tax: 18% Goods and Services Tax (GST) on payment processing service fee
        val taxGst = merchantFee.multiply(BigDecimal(config.gstRatePercent.toString()))
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        // 5. Promotional Cashback (legitimate merchant or partner funded reward)
        val cashback = if (isCashbackEligible) {
            val calc = amount.multiply(BigDecimal(config.cashbackRatePercent.toString()))
                .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
            calc.min(BigDecimal(config.maxCashbackAmount.toString()))
        } else {
            BigDecimal.ZERO
        }

        // 6. Net Settlement to Merchant = Amount - Merchant Fee - Tax on Fee - Refunds
        val deductions = merchantFee.add(taxGst).add(refund)
        val netSettlement = amount.subtract(deductions).max(BigDecimal.ZERO)

        // 7. Payer Payable Amount: UPI regulations mandate 0 hidden convenience fees for consumers
        val finalPayable = amount

        return RevenueCalculation(
            transactionAmount = amount.toDouble(),
            partnerFee = partnerFee.toDouble(),
            merchantFee = merchantFee.toDouble(),
            platformRevenue = platformRevenue.toDouble(),
            taxGst = taxGst.toDouble(),
            cashback = cashback.toDouble(),
            refundAmount = refund.toDouble(),
            netSettlement = netSettlement.toDouble(),
            payerConvenienceFee = 0.0,
            finalPayableAmount = finalPayable.toDouble()
        )
    }
}
