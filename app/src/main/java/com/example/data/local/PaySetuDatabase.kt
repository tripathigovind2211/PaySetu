package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.AdminDao
import com.example.data.local.dao.MerchantDao
import com.example.data.local.dao.PaymentDao
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.CashbackLedgerEntity
import com.example.data.local.entities.DisputeEntity
import com.example.data.local.entities.FraudAlertEntity
import com.example.data.local.entities.MerchantEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.RefundEntity
import com.example.data.local.entities.RevenueLedgerEntity
import com.example.data.local.entities.SettlementEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.WebhookEntity

@Database(
    entities = [
        UserEntity::class,
        BankAccountEntity::class,
        MerchantEntity::class,
        OrderEntity::class,
        PaymentEntity::class,
        RefundEntity::class,
        SettlementEntity::class,
        DisputeEntity::class,
        FraudAlertEntity::class,
        CashbackLedgerEntity::class,
        RevenueLedgerEntity::class,
        AuditLogEntity::class,
        WebhookEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PaySetuDatabase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao
    abstract fun merchantDao(): MerchantDao
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile
        private var INSTANCE: PaySetuDatabase? = null

        fun getInstance(context: Context): PaySetuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaySetuDatabase::class.java,
                    "paysetu_secure_ledger.db"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
