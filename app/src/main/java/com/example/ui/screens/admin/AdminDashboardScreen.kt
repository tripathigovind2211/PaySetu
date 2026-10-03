package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.FraudAlertEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.RevenueLedgerEntity
import com.example.domain.model.ComplianceRequirement
import com.example.domain.model.ComplianceStatus
import com.example.domain.model.ReconciliationSummary
import com.example.domain.model.RevenueConfiguration
import com.example.domain.model.RiskLevel
import com.example.ui.components.RiskChip
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberReward
import com.example.ui.theme.BharatSaffron
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CrimsonSoftBg
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    revenueLedgers: List<RevenueLedgerEntity>,
    fraudAlerts: List<FraudAlertEntity>,
    auditLogs: List<AuditLogEntity>,
    payments: List<PaymentEntity>,
    reconSummary: ReconciliationSummary?,
    complianceList: List<ComplianceRequirement>,
    revenueConfig: RevenueConfiguration,
    onRunRecon: () -> Unit,
    onUpdateRevenueConfig: (RevenueConfiguration) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Revenue Ledger", "Fraud & Risk", "3-Way Recon", "Compliance", "Audit Logs")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
            .testTag("admin_dashboard_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavyDark,
            contentColor = EmeraldLight
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) EmeraldLight else Slate400
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> AdminRevenueLedgerTab(revenueLedgers, revenueConfig, onUpdateRevenueConfig)
            1 -> AdminFraudAndRiskTab(fraudAlerts)
            2 -> AdminReconciliationTab(reconSummary, onRunRecon)
            3 -> AdminComplianceTab(complianceList)
            4 -> AdminAuditLogsTab(auditLogs)
        }
    }
}

@Composable
fun AdminRevenueLedgerTab(
    ledgers: List<RevenueLedgerEntity>,
    config: RevenueConfiguration,
    onUpdateConfig: (RevenueConfiguration) -> Unit
) {
    var isEditingConfig by remember { mutableStateOf(false) }
    var partnerFeeInput by remember { mutableStateOf(config.partnerFeePercent.toString()) }
    var merchantFeeInput by remember { mutableStateOf(config.merchantFeePercent.toString()) }
    var gstInput by remember { mutableStateOf(config.gstRatePercent.toString()) }
    var cashbackInput by remember { mutableStateOf(config.cashbackRatePercent.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Configurable Revenue Engine", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                            Text("Regulated non-predatory fee schedule (No hardcoded 5%)", fontSize = 11.sp, color = Slate400)
                        }
                        Button(
                            onClick = { isEditingConfig = !isEditingConfig },
                            modifier = Modifier.height(34.dp).testTag("btn_toggle_config_edit"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Text(if (isEditingConfig) "Close" else "Edit Rates", fontSize = 11.sp)
                        }
                    }

                    if (isEditingConfig) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = merchantFeeInput,
                                onValueChange = { merchantFeeInput = it },
                                label = { Text("MDR (%)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = partnerFeeInput,
                                onValueChange = { partnerFeeInput = it },
                                label = { Text("PSP Bank (%)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = gstInput,
                                onValueChange = { gstInput = it },
                                label = { Text("GST (%)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cashbackInput,
                                onValueChange = { cashbackInput = it },
                                label = { Text("Cashback (%)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val updated = config.copy(
                                    merchantFeePercent = merchantFeeInput.toDoubleOrNull() ?: config.merchantFeePercent,
                                    partnerFeePercent = partnerFeeInput.toDoubleOrNull() ?: config.partnerFeePercent,
                                    gstRatePercent = gstInput.toDoubleOrNull() ?: config.gstRatePercent,
                                    cashbackRatePercent = cashbackInput.toDoubleOrNull() ?: config.cashbackRatePercent
                                )
                                onUpdateConfig(updated)
                                isEditingConfig = false
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_save_revenue_rates"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Text("Save Compliant Rate Configuration", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Merchant MDR: ${config.merchantFeePercent}%", fontSize = 12.sp, color = Slate700)
                            Text("Partner PSP Fee: ${config.partnerFeePercent}%", fontSize = 12.sp, color = Slate700)
                            Text("GST Rate: ${config.gstRatePercent}%", fontSize = 12.sp, color = Slate700)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Immutable Financial Revenue Ledger", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Append-only double-entry financial trail. Zero direct modification.", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(ledgers) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.ledgerEntryId, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                        Text("Txn: ₹${String.format("%.2f", entry.transactionAmount)}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Partner Fee: ₹${String.format("%.2f", entry.partnerFee)}", fontSize = 11.sp, color = Slate700)
                        Text("MDR: ₹${String.format("%.2f", entry.merchantFee)}", fontSize = 11.sp, color = Slate700)
                        Text("Platform Net: ₹${String.format("%.2f", entry.platformRevenue)}", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST (18%): ₹${String.format("%.2f", entry.taxGst)}", fontSize = 10.sp, color = Slate400)
                        Text("Net Settlement: ₹${String.format("%.2f", entry.netSettlementToMerchant)}", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFraudAndRiskTab(fraudAlerts: List<FraudAlertEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text("Fraud & Risk Engine Alerts", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Real-time velocity monitoring, device fingerprint binding, and RBI limit enforcement", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (fraudAlerts.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("All transactions within normal risk thresholds (0 active alerts).", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(fraudAlerts) { alert ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(alert.triggerRule, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CrimsonDanger)
                            RiskChip(level = alert.riskLevel)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Action Taken: ${alert.actionTaken}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        Text(alert.details, fontSize = 11.sp, color = Slate700)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReconciliationTab(
    summary: ReconciliationSummary?,
    onRunRecon: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Automated 3-Way Reconciliation", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                            Text("Internal Ledger ⟷ PSP Gateway ⟷ Bank Clearing", fontSize = 11.sp, color = Slate400)
                        }
                        Button(
                            onClick = onRunRecon,
                            modifier = Modifier.height(36.dp).testTag("btn_run_recon"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Recon", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    summary?.let { s ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = EmeraldSoftBg)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Matched", fontSize = 10.sp, color = Slate700)
                                    Text("${s.matchedTransactions} / ${s.totalTransactions}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Discrepancies", fontSize = 10.sp, color = Slate700)
                                    Text("${s.amountMismatches + s.missingInternal + s.missingProvider}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CrimsonDanger)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Reconciled Volume: ₹${String.format("%.2f", s.totalReconciledVolume)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        Text("Discrepancy Amount: ₹${String.format("%.2f", s.discrepancyAmount)}", fontSize = 12.sp, color = if (s.discrepancyAmount > 0) CrimsonDanger else EmeraldSuccess)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminComplianceTab(complianceList: List<ComplianceRequirement>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text("Regulatory & Statutory Compliance Checklist", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Mandatory Indian Digital Payment Controls & Licensing Status", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(complianceList) { item ->
            val isNotReady = item.status == ComplianceStatus.NOT_PRODUCTION_READY
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNotReady) Color(0xFFFFFBEB) else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isNotReady) BharatSaffron else CardBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.authority, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyPrimary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isNotReady) Color(0xFFFEE2E2) else EmeraldSoftBg
                        ) {
                            Text(
                                item.status.name.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNotReady) CrimsonDanger else EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Text(item.description, fontSize = 11.sp, color = Slate700)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Audit Note: ${item.auditNote}", fontSize = 11.sp, color = Slate400)
                }
            }
        }
    }
}

@Composable
fun AdminAuditLogsTab(auditLogs: List<AuditLogEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text("Immutable Security Audit Logs", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Privileged actor tracking with IP addresses and cryptographic references", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                        Text(log.actorType, fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                    }
                    Text(log.details, fontSize = 11.sp, color = Slate700)
                    Text("Actor: ${log.actorId} • IP: ${log.ipAddress}", fontSize = 10.sp, color = Slate400)
                }
            }
        }
    }
}
