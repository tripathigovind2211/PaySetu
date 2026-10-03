package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.local.entities.PaymentEntity
import com.example.domain.model.EnvironmentMode
import com.example.domain.model.UserRole
import com.example.ui.components.PaySetuHeader
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.consumer.ConsumerHomeScreen
import com.example.ui.screens.consumer.DisputeScreen
import com.example.ui.screens.consumer.KycScreen
import com.example.ui.screens.consumer.PaymentCheckoutDialog
import com.example.ui.screens.consumer.PaymentReceiptScreen
import com.example.ui.screens.consumer.ScanQrScreen
import com.example.ui.screens.developer.DeveloperDocsScreen
import com.example.ui.screens.merchant.MerchantDashboardScreen

enum class ConsumerSubScreen {
    HOME,
    SCAN_QR,
    RECEIPT,
    KYC,
    DISPUTE
}

@Composable
fun PaySetuApp(viewModel: PaymentViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val envMode by viewModel.envMode.collectAsState()
    val uiNotice by viewModel.uiNotice.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val refunds by viewModel.refunds.collectAsState()
    val disputes by viewModel.disputes.collectAsState()
    val fraudAlerts by viewModel.fraudAlerts.collectAsState()
    val revenueLedgers by viewModel.revenueLedgers.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val totalCashback by viewModel.totalCashback.collectAsState()
    val defaultMerchant by viewModel.defaultMerchant.collectAsState()
    val webhooks by viewModel.webhooks.collectAsState()
    val reconSummary by viewModel.reconSummary.collectAsState()
    val revenueConfig by viewModel.revenueConfig.collectAsState()
    val activeCheckout by viewModel.checkoutState.collectAsState()
    val lastPayment by viewModel.lastPayment.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiNotice) {
        uiNotice?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiNotice()
        }
    }

    var consumerScreen by remember { mutableStateOf(ConsumerSubScreen.HOME) }
    var selectedDisputePayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var selectedReceiptPayment by remember { mutableStateOf<PaymentEntity?>(null) }

    // Android back navigation
    BackHandler(enabled = consumerScreen != ConsumerSubScreen.HOME) {
        consumerScreen = ConsumerSubScreen.HOME
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("paysetu_main_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persistent Brand & Role Header
            PaySetuHeader(
                currentRole = currentRole,
                onRoleSelected = { role ->
                    viewModel.setRole(role)
                    consumerScreen = ConsumerSubScreen.HOME
                },
                envMode = envMode,
                onEnvModeToggle = {
                    val nextMode = when (envMode) {
                        EnvironmentMode.DEMO -> EnvironmentMode.SANDBOX
                        EnvironmentMode.SANDBOX -> EnvironmentMode.PRODUCTION
                        EnvironmentMode.PRODUCTION -> EnvironmentMode.DEMO
                    }
                    viewModel.setEnvironmentMode(nextMode)
                }
            )

            // Body content according to selected Role
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (currentRole) {
                    UserRole.CONSUMER -> {
                        when (consumerScreen) {
                            ConsumerSubScreen.HOME -> {
                                ConsumerHomeScreen(
                                    user = currentUser,
                                    bankAccounts = bankAccounts,
                                    payments = payments,
                                    totalCashback = totalCashback ?: 0.0,
                                    onScanQrClick = { consumerScreen = ConsumerSubScreen.SCAN_QR },
                                    onPayVpaClick = { consumerScreen = ConsumerSubScreen.SCAN_QR },
                                    onKycClick = { consumerScreen = ConsumerSubScreen.KYC },
                                    onDisputeClick = { p ->
                                        selectedDisputePayment = p
                                        consumerScreen = ConsumerSubScreen.DISPUTE
                                    },
                                    onPaymentClick = { p ->
                                        selectedReceiptPayment = p
                                        consumerScreen = ConsumerSubScreen.RECEIPT
                                    }
                                )
                            }
                            ConsumerSubScreen.SCAN_QR -> {
                                ScanQrScreen(
                                    onBack = { consumerScreen = ConsumerSubScreen.HOME },
                                    onQrScanned = { name, vpa, amount, note ->
                                        viewModel.startCheckout(name, vpa, amount, note)
                                    }
                                )
                            }
                            ConsumerSubScreen.RECEIPT -> {
                                val p = selectedReceiptPayment ?: lastPayment ?: payments.firstOrNull()
                                if (p != null) {
                                    PaymentReceiptScreen(
                                        payment = p,
                                        onDoneClick = { consumerScreen = ConsumerSubScreen.HOME },
                                        onRaiseDisputeClick = { target ->
                                            selectedDisputePayment = target
                                            consumerScreen = ConsumerSubScreen.DISPUTE
                                        }
                                    )
                                } else {
                                    consumerScreen = ConsumerSubScreen.HOME
                                }
                            }
                            ConsumerSubScreen.KYC -> {
                                KycScreen(
                                    user = currentUser,
                                    onBack = { consumerScreen = ConsumerSubScreen.HOME },
                                    onUpgradeKyc = { viewModel.upgradeKyc() }
                                )
                            }
                            ConsumerSubScreen.DISPUTE -> {
                                DisputeScreen(
                                    disputes = disputes,
                                    targetPayment = selectedDisputePayment,
                                    onBack = { consumerScreen = ConsumerSubScreen.HOME },
                                    onSubmitDispute = { pid, cat, remarks ->
                                        viewModel.fileDispute(pid, cat, remarks)
                                    }
                                )
                            }
                        }
                    }
                    UserRole.MERCHANT -> {
                        MerchantDashboardScreen(
                            merchant = defaultMerchant,
                            orders = orders,
                            payments = payments,
                            refunds = refunds,
                            webhooks = webhooks,
                            onCreateOrder = { amt, desc, phone, email, onDone ->
                                viewModel.createMerchantOrder(amt, desc, phone, email, onDone)
                            },
                            onInitiateRefund = { pid, reason ->
                                viewModel.processRefund(pid, reason)
                            }
                        )
                    }
                    UserRole.ADMIN -> {
                        AdminDashboardScreen(
                            revenueLedgers = revenueLedgers,
                            fraudAlerts = fraudAlerts,
                            auditLogs = auditLogs,
                            payments = payments,
                            reconSummary = reconSummary,
                            complianceList = viewModel.getComplianceChecklist(),
                            revenueConfig = revenueConfig,
                            onRunRecon = { viewModel.runReconciliation() },
                            onUpdateRevenueConfig = { cfg -> viewModel.updateRevenueConfig(cfg) }
                        )
                    }
                    UserRole.DEVELOPER -> {
                        DeveloperDocsScreen()
                    }
                }
            }
        }

        // Active Checkout Sheet (Dialog)
        activeCheckout?.let { checkoutState ->
            PaymentCheckoutDialog(
                state = checkoutState,
                bankAccounts = bankAccounts,
                onDismiss = { viewModel.dismissCheckout() },
                onAuthorizePayment = {
                    viewModel.confirmPayment(
                        onSuccess = {
                            selectedReceiptPayment = viewModel.lastPayment.value
                            consumerScreen = ConsumerSubScreen.RECEIPT
                        }
                    )
                }
            )
        }
    }
}
