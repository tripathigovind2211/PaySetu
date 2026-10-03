package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.CashbackLedgerEntity
import com.example.data.local.entities.DisputeEntity
import com.example.data.local.entities.FraudAlertEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.RefundEntity
import com.example.data.local.entities.RevenueLedgerEntity
import com.example.data.local.entities.SettlementEntity
import com.example.data.local.entities.UserEntity
import com.example.domain.model.TransactionState
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    // Payments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPaymentsFlow(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    suspend fun getPaymentById(paymentId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE transactionRef = :utr")
    suspend fun getPaymentByUtr(utr: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE payerUserId = :userId ORDER BY timestamp DESC")
    fun getPaymentsByUserFlow(userId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE orderId = :orderId")
    suspend fun getPaymentByOrderId(orderId: String): PaymentEntity?

    @Query("UPDATE payments SET status = :status WHERE paymentId = :paymentId")
    suspend fun updatePaymentStatus(paymentId: String, status: TransactionState)

    // Orders
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    // Refunds
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefund(refund: RefundEntity)

    @Query("SELECT * FROM refunds ORDER BY timestamp DESC")
    fun getAllRefundsFlow(): Flow<List<RefundEntity>>

    @Query("SELECT * FROM refunds WHERE paymentId = :paymentId")
    suspend fun getRefundsByPaymentId(paymentId: String): List<RefundEntity>

    // Settlements
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Query("SELECT * FROM settlements ORDER BY timestamp DESC")
    fun getAllSettlementsFlow(): Flow<List<SettlementEntity>>

    // Disputes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeEntity)

    @Query("SELECT * FROM disputes ORDER BY filedAt DESC")
    fun getAllDisputesFlow(): Flow<List<DisputeEntity>>

    // Fraud Alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFraudAlert(alert: FraudAlertEntity)

    @Query("SELECT * FROM fraud_alerts ORDER BY timestamp DESC")
    fun getAllFraudAlertsFlow(): Flow<List<FraudAlertEntity>>

    // Ledgers - Immutable entries
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRevenueLedger(ledger: RevenueLedgerEntity)

    @Query("SELECT * FROM revenue_ledger ORDER BY recordedAt DESC")
    fun getAllRevenueLedgersFlow(): Flow<List<RevenueLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashback(cashback: CashbackLedgerEntity)

    @Query("SELECT * FROM cashback_ledger WHERE userId = :userId ORDER BY timestamp DESC")
    fun getCashbackByUserFlow(userId: String): Flow<List<CashbackLedgerEntity>>

    @Query("SELECT SUM(amount) FROM cashback_ledger WHERE userId = :userId AND status = 'CREDITED'")
    fun getTotalCashbackFlow(userId: String): Flow<Double?>

    // Users & Bank Accounts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccountEntity)

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId")
    fun getBankAccountsFlow(userId: String): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId AND isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryBankAccount(userId: String): BankAccountEntity?
}
