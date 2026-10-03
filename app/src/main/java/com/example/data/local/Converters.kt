package com.example.data.local

import androidx.room.TypeConverter
import com.example.domain.model.DisputeStatus
import com.example.domain.model.KycStatus
import com.example.domain.model.PaymentMode
import com.example.domain.model.RiskLevel
import com.example.domain.model.TransactionState

class Converters {
    @TypeConverter
    fun fromTransactionState(state: TransactionState): String = state.name

    @TypeConverter
    fun toTransactionState(value: String): TransactionState =
        runCatching { TransactionState.valueOf(value) }.getOrDefault(TransactionState.PENDING)

    @TypeConverter
    fun fromRiskLevel(risk: RiskLevel): String = risk.name

    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel =
        runCatching { RiskLevel.valueOf(value) }.getOrDefault(RiskLevel.LOW)

    @TypeConverter
    fun fromKycStatus(status: KycStatus): String = status.name

    @TypeConverter
    fun toKycStatus(value: String): KycStatus =
        runCatching { KycStatus.valueOf(value) }.getOrDefault(KycStatus.MIN_KYC)

    @TypeConverter
    fun fromPaymentMode(mode: PaymentMode): String = mode.name

    @TypeConverter
    fun toPaymentMode(value: String): PaymentMode =
        runCatching { PaymentMode.valueOf(value) }.getOrDefault(PaymentMode.UPI_QR)

    @TypeConverter
    fun fromDisputeStatus(status: DisputeStatus): String = status.name

    @TypeConverter
    fun toDisputeStatus(value: String): DisputeStatus =
        runCatching { DisputeStatus.valueOf(value) }.getOrDefault(DisputeStatus.OPEN)
}
