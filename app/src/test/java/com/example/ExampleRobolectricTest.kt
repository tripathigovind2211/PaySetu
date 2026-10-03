package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.engine.ExternalPspRecord
import com.example.domain.engine.FraudRiskEngine
import com.example.domain.engine.PspBridgeService
import com.example.domain.engine.ReconciliationEngine
import com.example.domain.engine.RevenueEngine
import com.example.domain.model.KycStatus
import com.example.domain.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PaySetu", appName)
    }

    @Test
    fun `revenue engine calculates itemized fees and 18 percent gst correctly`() {
        val engine = RevenueEngine()
        val calc = engine.calculate(transactionAmount = 1000.0, isCashbackEligible = true)

        assertEquals(1000.0, calc.transactionAmount, 0.01)
        // 0.85% MDR on 1000 = 8.50
        assertEquals(8.50, calc.merchantFee, 0.01)
        // 0.15% Partner Fee on 1000 = 1.50
        assertEquals(1.50, calc.partnerFee, 0.01)
        // Platform Net = 8.50 - 1.50 = 7.00
        assertEquals(7.00, calc.platformRevenue, 0.01)
        // 18% GST on 8.50 MDR = 1.53
        assertEquals(1.53, calc.taxGst, 0.01)
        // Zero convenience fee for payer
        assertEquals(0.0, calc.payerConvenienceFee, 0.01)
        assertEquals(1000.0, calc.finalPayableAmount, 0.01)
        // Net settlement = 1000 - 8.50 - 1.53 = 989.97
        assertEquals(989.97, calc.netSettlement, 0.01)
    }

    @Test
    fun `fraud engine blocks transactions exceeding RBI Min-KYC limit`() {
        val fraudEngine = FraudRiskEngine()
        val result = fraudEngine.evaluateTransaction(
            amount = 15000.0, // Exceeds 10,000 Min KYC limit
            kycStatus = KycStatus.MIN_KYC,
            recentTxCountLastHour = 1,
            recentFailedCount = 0,
            isDeviceBindingValid = true,
            isDuplicateIdempotency = false
        )

        assertEquals(RiskLevel.BLOCKED, result.riskLevel)
        assertFalse(result.allowProcessing)
        assertTrue(result.triggeredRules.contains("RBI_MIN_KYC_TRANSACTION_LIMIT_EXCEEDED"))
    }

    @Test
    fun `psp bridge correctly parses and generates NPCI UPI QR URI`() {
        val psp = PspBridgeService()
        val uri = psp.generateUpiUri(
            payeeVpa = "merchant@paysetu",
            payeeName = "Merchant Store",
            amount = 250.0,
            orderId = "ord_9981",
            transactionNote = "Groceries"
        )
        assertTrue(uri.startsWith("upi://pay?"))

        val parsed = psp.parseUpiUri(uri)
        assertNotNull(parsed)
        assertEquals("merchant@paysetu", parsed?.payeeVpa)
        assertEquals("Merchant Store", parsed?.payeeName)
        assertEquals(250.0, parsed?.amount ?: 0.0, 0.01)
    }

    @Test
    fun `reconciliation engine identifies discrepancies`() {
        val reconEngine = ReconciliationEngine()
        val (summary, discrepancies) = reconEngine.performThreeWayReconciliation(
            internalPayments = emptyList(),
            externalPspRecords = listOf(
                ExternalPspRecord("4289001", 500.0, "SUCCESS", "BRN_1", 1000L)
            )
        )
        assertEquals(1, summary.missingInternal)
        assertEquals(1, discrepancies.size)
    }
}
