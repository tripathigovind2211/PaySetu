package com.example.domain.engine

import android.net.Uri
import com.example.domain.model.EnvironmentMode
import com.example.domain.model.TransactionState
import com.example.domain.model.UpiQrPayload
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

data class PspPaymentResponse(
    val success: Boolean,
    val transactionRef: String, // 12-digit UPI UTR / RRN
    val status: TransactionState,
    val failureMessage: String? = null,
    val bankAuthCode: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProductionReadinessCheck(
    val isReady: Boolean,
    val missingItems: List<String>
)

class PspBridgeService {

    var currentMode: EnvironmentMode = EnvironmentMode.DEMO

    /**
     * Parses standard NPCI UPI QR code deep link
     * Example: upi://pay?pa=merchant@paysetu&pn=Merchant+Store&am=250.00&cu=INR&tn=Order101
     */
    fun parseUpiUri(rawUriString: String): UpiQrPayload? {
        return try {
            val uri = Uri.parse(rawUriString)
            if (uri.scheme?.lowercase() != "upi") return null

            val payeeVpa = uri.getQueryParameter("pa") ?: return null
            val payeeName = uri.getQueryParameter("pn") ?: payeeVpa.substringBefore("@")
            val amountStr = uri.getQueryParameter("am")
            val amount = amountStr?.toDoubleOrNull()
            val note = uri.getQueryParameter("tn")
            val ref = uri.getQueryParameter("tr")
            val mcc = uri.getQueryParameter("mc")

            UpiQrPayload(
                payeeVpa = payeeVpa,
                payeeName = payeeName,
                amount = amount,
                transactionNote = note,
                transactionRef = ref,
                merchantCategoryCode = mcc,
                url = rawUriString
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generates a standard NPCI compliant UPI Deep Link URI
     */
    fun generateUpiUri(
        payeeVpa: String,
        payeeName: String,
        amount: Double,
        orderId: String,
        transactionNote: String
    ): String {
        val encodedName = Uri.encode(payeeName)
        val encodedNote = Uri.encode(transactionNote)
        val formattedAmount = String.format(java.util.Locale.US, "%.2f", amount)
        return "upi://pay?pa=$payeeVpa&pn=$encodedName&am=$formattedAmount&cu=INR&tn=$encodedNote&tr=$orderId"
    }

    /**
     * Validates if production mode can safely process live funds.
     */
    fun checkProductionReadiness(
        partnerApiKey: String?,
        pspBankId: String?,
        hsmKeyInstalled: Boolean
    ): ProductionReadinessCheck {
        val missing = mutableListOf<String>()
        if (partnerApiKey.isNullOrBlank() || partnerApiKey.startsWith("test_") || partnerApiKey.startsWith("demo_")) {
            missing.add("Authorised PSP Bank Live API Credentials (missing production key)")
        }
        if (pspBankId.isNullOrBlank()) {
            missing.add("Partner Acquirer PSP Bank Agreement & Merchant Identification Number (MID)")
        }
        if (!hsmKeyInstalled) {
            missing.add("NPCI UPI Common Library (CL) Hardware Security Module (HSM) Keys")
        }
        missing.add("Reserve Bank of India (RBI) Payment Aggregator (PA) In-Principle / Final Authorisation")
        missing.add("Third Party Application Provider (TPAP) Registration with NPCI")

        return ProductionReadinessCheck(
            isReady = false, // Always guard live money without regulated sponsor bank
            missingItems = missing
        )
    }

    /**
     * Executes payment through the authorised PSP flow.
     * Never accepts raw UPI PIN; delegating to authorised PSP Common Library.
     */
    suspend fun processPayment(
        amount: Double,
        payeeVpa: String,
        payerVpa: String,
        idempotencyKey: String
    ): PspPaymentResponse {
        if (currentMode == EnvironmentMode.PRODUCTION) {
            val readiness = checkProductionReadiness(null, null, false)
            return PspPaymentResponse(
                success = false,
                transactionRef = "",
                status = TransactionState.FAILED,
                failureMessage = "NOT PRODUCTION READY: ${readiness.missingItems.first()}"
            )
        }

        // Generate authentic 12-digit Indian UPI UTR / RRN (Bank Reference Number)
        // Format: Year (1) + Day of year (3) + Hour (2) + Random sequence (6)
        val utrPrefix = "4289" // Standard Indian bank acquirer switch code
        val randomSuffix = Random.nextLong(10000000, 99999999).toString()
        val generatedUtr = "$utrPrefix$randomSuffix"

        // In Demo/Sandbox mode, simulate authentic bank switch latency
        kotlinx.coroutines.delay(1200)

        return PspPaymentResponse(
            success = true,
            transactionRef = generatedUtr,
            status = TransactionState.SUCCESS,
            bankAuthCode = "AUTH_${Random.nextInt(100000, 999999)}"
        )
    }

    /**
     * Generates HMAC-SHA256 signature for server-to-server merchant webhook verification
     */
    fun generateHmacSignature(payload: String, secret: String): String {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val key = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256")
            mac.init(key)
            val bytes = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
}
