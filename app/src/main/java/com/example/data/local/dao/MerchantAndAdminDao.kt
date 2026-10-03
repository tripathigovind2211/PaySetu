package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.MerchantEntity
import com.example.data.local.entities.WebhookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchant(merchant: MerchantEntity)

    @Update
    suspend fun updateMerchant(merchant: MerchantEntity)

    @Query("SELECT * FROM merchants WHERE merchantId = :merchantId")
    suspend fun getMerchantById(merchantId: String): MerchantEntity?

    @Query("SELECT * FROM merchants LIMIT 1")
    fun getDefaultMerchantFlow(): Flow<MerchantEntity?>

    @Query("SELECT * FROM merchants")
    fun getAllMerchantsFlow(): Flow<List<MerchantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebhook(webhook: WebhookEntity)

    @Query("SELECT * FROM webhooks ORDER BY timestamp DESC")
    fun getAllWebhooksFlow(): Flow<List<WebhookEntity>>
}

@Dao
interface AdminDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Query("SELECT COUNT(*) FROM payments")
    fun getTotalTransactionsCountFlow(): Flow<Int>

    @Query("SELECT SUM(amount) FROM payments WHERE status = 'SUCCESS'")
    fun getTotalSettledVolumeFlow(): Flow<Double?>

    @Query("SELECT SUM(platformRevenue) FROM revenue_ledger")
    fun getTotalPlatformRevenueFlow(): Flow<Double?>
}
