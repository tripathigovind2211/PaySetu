package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.DisputeEntity
import com.example.data.local.entities.FraudAlertEntity
import com.example.data.local.entities.MerchantEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.RefundEntity
import com.example.data.local.entities.RevenueLedgerEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.WebhookEntity
import com.example.data.repository.PaymentRepository
import com.example.domain.model.ComplianceRequirement
import com.example.domain.model.EnvironmentMode
import com.example.domain.model.PaymentMode
import com.example.domain.model.ReconciliationSummary
import com.example.domain.model.RevenueCalculation
import com.example.domain.model.RevenueConfiguration
import com.example.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveCheckoutState(
    val payeeName: String = "",
    val payeeVpa: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val orderId: String? = null,
    val calculation: RevenueCalculation? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

class PaymentViewModel(
    val repository: PaymentRepository
) : ViewModel() {

    // Active Role in the App
    private val _currentRole = MutableStateFlow(UserRole.CONSUMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Environment Mode
    private val _envMode = MutableStateFlow(EnvironmentMode.DEMO)
    val envMode: StateFlow<EnvironmentMode> = _envMode.asStateFlow()

    // Active Payment Checkout state
    private val _checkoutState = MutableStateFlow<ActiveCheckoutState?>(null)
    val checkoutState: StateFlow<ActiveCheckoutState?> = _checkoutState.asStateFlow()

    // Last completed payment (for Receipt screen)
    private val _lastPayment = MutableStateFlow<PaymentEntity?>(null)
    val lastPayment: StateFlow<PaymentEntity?> = _lastPayment.asStateFlow()

    // Toast/Snackbar notification
    private val _uiNotice = MutableStateFlow<String?>(null)
    val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

    // Reconciliation Summary state
    private val _reconSummary = MutableStateFlow<ReconciliationSummary?>(null)
    val reconSummary: StateFlow<ReconciliationSummary?> = _reconSummary.asStateFlow()

    // Revenue Config State
    private val _revenueConfig = MutableStateFlow(repository.revenueEngine.getCurrentConfig())
    val revenueConfig: StateFlow<RevenueConfiguration> = _revenueConfig.asStateFlow()

    // Repository Data Flows
    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bankAccounts: StateFlow<List<BankAccountEntity>> = repository.getBankAccountsFlow("usr_rohit_77")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = repository.allPaymentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrdersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val refunds: StateFlow<List<RefundEntity>> = repository.allRefundsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disputes: StateFlow<List<DisputeEntity>> = repository.allDisputesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fraudAlerts: StateFlow<List<FraudAlertEntity>> = repository.allFraudAlertsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val revenueLedgers: StateFlow<List<RevenueLedgerEntity>> = repository.allRevenueLedgerFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCashback: StateFlow<Double?> = repository.getCashbackFlow("usr_rohit_77")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val defaultMerchant: StateFlow<MerchantEntity?> = repository.defaultMerchantFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMerchants: StateFlow<List<MerchantEntity>> = repository.allMerchantsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val webhooks: StateFlow<List<WebhookEntity>> = repository.allWebhooksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initSeedDataIfEmpty()
            runReconciliation()
        }
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun setEnvironmentMode(mode: EnvironmentMode) {
        _envMode.value = mode
        repository.pspBridgeService.currentMode = mode
        if (mode == EnvironmentMode.PRODUCTION) {
            _uiNotice.value = "PRODUCTION MODE: Live bank sponsor & RBI PA approvals required. Live card/PIN processing is gated."
        } else {
            _uiNotice.value = "Switched to ${mode.name} MODE."
        }
    }

    fun clearUiNotice() {
        _uiNotice.value = null
    }

    fun startCheckout(payeeName: String, payeeVpa: String, amount: Double, note: String = "", orderId: String? = null) {
        val calc = repository.revenueEngine.calculate(amount, isCashbackEligible = true)
        _checkoutState.value = ActiveCheckoutState(
            payeeName = payeeName,
            payeeVpa = payeeVpa,
            amount = amount,
            note = note,
            orderId = orderId,
            calculation = calc
        )
    }

    fun dismissCheckout() {
        _checkoutState.value = null
    }

    fun confirmPayment(onSuccess: () -> Unit) {
        val current = _checkoutState.value ?: return
        val user = currentUser.value ?: return

        _checkoutState.value = current.copy(isProcessing = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.executePayment(
                payerUserId = user.userId,
                payerVpa = "rohit@paysetu",
                payeeName = current.payeeName,
                payeeVpa = current.payeeVpa,
                amount = current.amount,
                orderId = current.orderId,
                mode = PaymentMode.UPI_QR
            )

            if (result.isSuccess) {
                val payment = result.getOrNull()
                _lastPayment.value = payment
                _checkoutState.value = null
                onSuccess()
            } else {
                _checkoutState.value = current.copy(
                    isProcessing = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Transaction failed"
                )
            }
        }
    }

    fun createMerchantOrder(amount: Double, description: String, phone: String, email: String, onCreated: (OrderEntity) -> Unit) {
        viewModelScope.launch {
            val merchant = defaultMerchant.value
            val merchantId = merchant?.merchantId ?: "mcht_generic"
            val order = repository.createMerchantOrder(merchantId, amount, description, phone, email)
            _uiNotice.value = "Order created: ${order.orderId} (₹$amount)"
            onCreated(order)
        }
    }

    fun processRefund(paymentId: String, reason: String) {
        viewModelScope.launch {
            val result = repository.processRefund(paymentId, reason)
            if (result.isSuccess) {
                _uiNotice.value = "Refund processed successfully (Ref: ${result.getOrNull()?.pspRefundRef})"
            } else {
                _uiNotice.value = "Refund failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun fileDispute(paymentId: String, category: String, remarks: String) {
        viewModelScope.launch {
            val disp = repository.fileDispute(paymentId, category, remarks)
            _uiNotice.value = "Dispute filed with NPCI ODR (Ref: ${disp.disputeRefNo})"
        }
    }

    fun upgradeKyc() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.upgradeUserToFullKyc(user.userId)
            _uiNotice.value = "KYC successfully verified! Video-KYC and CKYCR approved."
        }
    }

    fun runReconciliation() {
        viewModelScope.launch {
            val summary = repository.runReconciliationJob()
            _reconSummary.value = summary
        }
    }

    fun updateRevenueConfig(newConfig: RevenueConfiguration) {
        repository.revenueEngine.updateConfig(newConfig)
        _revenueConfig.value = newConfig
        _uiNotice.value = "Revenue engine configuration updated."
    }

    fun getComplianceChecklist(): List<ComplianceRequirement> = repository.getComplianceChecklist()

    companion object {
        fun provideFactory(repository: PaymentRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PaymentViewModel(repository) as T
                }
            }
    }
}
