package com.example.ui.screens.merchant

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.MerchantEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.RefundEntity
import com.example.data.local.entities.WebhookEntity
import com.example.domain.model.TransactionState
import com.example.ui.components.TransactionStateChip
import com.example.ui.theme.AmberReward
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun MerchantDashboardScreen(
    merchant: MerchantEntity?,
    orders: List<OrderEntity>,
    payments: List<PaymentEntity>,
    refunds: List<RefundEntity>,
    webhooks: List<WebhookEntity>,
    onCreateOrder: (amount: Double, desc: String, phone: String, email: String, onDone: (OrderEntity) -> Unit) -> Unit,
    onInitiateRefund: (paymentId: String, reason: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Create Order", "Transactions", "Webhooks & API")

    var showRefundDialog by remember { mutableStateOf<PaymentEntity?>(null) }
    var refundReason by remember { mutableStateOf("Customer returned goods") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
            .testTag("merchant_dashboard_screen")
    ) {
        // Merchant Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = NavyPrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            merchant?.businessName ?: "Sharma Supermarket & Kirana",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            "VPA: ${merchant?.vpa ?: "sharmakirana@paysetu"} • MID: ${merchant?.merchantId ?: "mcht_sharma"}",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldSoftBg
                ) {
                    Text(
                        "KYC VERIFIED",
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = NavyPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("merchant_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> MerchantOverviewTab(payments, orders, refunds)
            1 -> MerchantCreateOrderTab(onCreateOrder)
            2 -> MerchantTransactionsTab(payments, onInitiateRefundClick = { showRefundDialog = it })
            3 -> MerchantWebhooksAndApiTab(merchant, webhooks)
        }
    }

    // Refund Confirmation Dialog
    showRefundDialog?.let { p ->
        AlertDialog(
            onDismissRequest = { showRefundDialog = null },
            title = { Text("Initiate Merchant Refund", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Payment ID: ${p.paymentId}", fontSize = 11.sp, color = Slate400)
                    Text("Amount to refund: ₹${String.format("%.2f", p.amount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = refundReason,
                        onValueChange = { refundReason = it },
                        label = { Text("Reason for Refund") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Money will be credited to original payer bank account within T+1 days via UPI refund switch.",
                        fontSize = 10.sp,
                        color = Slate700
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onInitiateRefund(p.paymentId, refundReason)
                        showRefundDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonDanger)
                ) {
                    Text("Confirm Refund", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRefundDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MerchantOverviewTab(
    payments: List<PaymentEntity>,
    orders: List<OrderEntity>,
    refunds: List<RefundEntity>
) {
    val totalGmv = payments.filter { it.status == TransactionState.SUCCESS }.sumOf { it.amount }
    val totalNetSettlement = payments.filter { it.status == TransactionState.SUCCESS }.sumOf { it.netSettlement }
    val totalMdr = payments.filter { it.status == TransactionState.SUCCESS }.sumOf { it.merchantFee }
    val totalGst = payments.filter { it.status == TransactionState.SUCCESS }.sumOf { it.taxGst }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Metrics Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MerchantMetricCard(
                    title = "Total GMV Volume",
                    value = "₹${String.format("%.2f", totalGmv)}",
                    subtitle = "${payments.size} payments",
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                MerchantMetricCard(
                    title = "Net Payout (T+1)",
                    value = "₹${String.format("%.2f", totalNetSettlement)}",
                    subtitle = "Bank escrow settled",
                    color = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MerchantMetricCard(
                    title = "MDR Fee (0.85%)",
                    value = "₹${String.format("%.2f", totalMdr)}",
                    subtitle = "Partner interchange",
                    color = Slate700,
                    modifier = Modifier.weight(1f)
                )
                MerchantMetricCard(
                    title = "GST on MDR (18%)",
                    value = "₹${String.format("%.2f", totalGst)}",
                    subtitle = "Govt statutory tax",
                    color = AmberReward,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Settlement Account & Escrow Schedule", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Nodal Escrow Bank: HDFC Bank Nodal Account (A/C: ••••••••••4912)", fontSize = 12.sp, color = Slate700)
                    Text("Settlement Cycle: T+1 Working Days (RBI Guidelines compliant)", fontSize = 12.sp, color = Slate700)
                    Text("Refund Window: 180 days from transaction date", fontSize = 12.sp, color = Slate700)
                }
            }
        }
    }
}

@Composable
fun MerchantMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = Slate700)
        }
    }
}

@Composable
fun MerchantCreateOrderTab(
    onCreateOrder: (amount: Double, desc: String, phone: String, email: String, onDone: (OrderEntity) -> Unit) -> Unit
) {
    var amountText by remember { mutableStateOf("450.00") }
    var descriptionText by remember { mutableStateOf("Groceries & Daily Essentials") }
    var phoneText by remember { mutableStateOf("+91 98765 43210") }
    var emailText by remember { mutableStateOf("customer@example.in") }
    var createdOrder by remember { mutableStateOf<OrderEntity?>(null) }

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
                    Text("Create Payment Order & Dynamic QR", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                    Text("Generate server-to-server payment order with shareable UPI link and dynamic QR", fontSize = 11.sp, color = Slate400)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Order Amount (₹)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_order_amount"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Order Description / Item Summary") },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_order_desc"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        label = { Text("Customer Mobile (for SMS payment link)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 100.0
                            onCreateOrder(amt, descriptionText, phoneText, emailText) { ord ->
                                createdOrder = ord
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_create_order_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Create Order & Generate UPI QR", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        createdOrder?.let { ord ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSoftBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Order Generated: ${ord.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                        Text("Amount: ₹${String.format("%.2f", ord.amount)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Deep Link: upi://pay?pa=sharmakirana@paysetu&pn=Sharma+Kirana&am=${ord.amount}&tr=${ord.orderId}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Slate700
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MerchantTransactionsTab(
    payments: List<PaymentEntity>,
    onInitiateRefundClick: (PaymentEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Merchant Payment Transactions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Server-verified transactions with settlement breakdown and refund options", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(payments) { payment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(payment.paymentId, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                            Text("UTR: ${payment.transactionRef}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Slate400)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${String.format("%.2f", payment.amount)}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Slate900)
                            TransactionStateChip(state = payment.status)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net Settlement: ₹${String.format("%.2f", payment.netSettlement)}", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.SemiBold)
                        Text("MDR Fee: ₹${String.format("%.2f", payment.merchantFee)}", fontSize = 11.sp, color = Slate700)
                        Text("GST (18%): ₹${String.format("%.2f", payment.taxGst)}", fontSize = 11.sp, color = Slate700)
                    }

                    if (payment.status == TransactionState.SUCCESS) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { onInitiateRefundClick(payment) },
                                modifier = Modifier.height(34.dp).testTag("btn_refund_${payment.paymentId}"),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Initiate Refund", fontSize = 11.sp, color = CrimsonDanger)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MerchantWebhooksAndApiTab(
    merchant: MerchantEntity?,
    webhooks: List<WebhookEntity>
) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Merchant API Credentials", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Production API Key (Header: X-Api-Key)", fontSize = 11.sp, color = Slate400)
                    Text(merchant?.apiKey ?: "ps_live_swg_98240a12", fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Slate900)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("HMAC-SHA256 Webhook Secret (Header: X-PaySetu-Signature)", fontSize = 11.sp, color = Slate400)
                    Text(merchant?.hmacSecret ?: "whsec_swiggy_7718293b", fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Slate900)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Configured Webhook URL", fontSize = 11.sp, color = Slate400)
                    Text(merchant?.webhookUrl ?: "https://api.merchant.com/webhook", fontSize = 12.sp, color = NavyPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recent Webhook Dispatches", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(webhooks) { wh ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(wh.eventType, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                        Surface(shape = RoundedCornerShape(4.dp), color = EmeraldSoftBg) {
                            Text("HTTP ${wh.responseCode} OK", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Payload: ${wh.payloadJson}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Slate700)
                    Text("HMAC: ${wh.signatureHmac.take(24)}...", fontSize = 10.sp, color = Slate400)
                }
            }
        }
    }
}
