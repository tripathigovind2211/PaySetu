package com.example.ui.screens.developer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.CardBorder
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

@Composable
fun DeveloperDocsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("OpenAPI REST", "Mobile SDKs", "Webhook HMAC", "Architecture")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
            .testTag("developer_docs_screen")
    ) {
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
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("dev_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> OpenApiRestTab()
            1 -> MobileSdksTab()
            2 -> WebhookHmacTab()
            3 -> ArchitectureTab()
        }
    }
}

@Composable
fun OpenApiRestTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text("PaySetu Payment Gateway REST APIs", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
            Text("Base URL: https://api.paysetu.in/v1 • Authenticate via X-Api-Key header", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            ApiEndpointCard(
                method = "POST",
                path = "/v1/orders",
                summary = "Create Payment Order",
                desc = "Creates a payment order and generates an authenticated UPI dynamic intent string."
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            ApiEndpointCard(
                method = "GET",
                path = "/v1/payments/{paymentId}",
                summary = "Verify Transaction Status",
                desc = "Independent server-to-server status verification. Never trust frontend callbacks."
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            ApiEndpointCard(
                method = "POST",
                path = "/v1/refunds",
                summary = "Initiate Refund",
                desc = "Initiates eligible full or partial refund to original payer VPA."
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            ApiEndpointCard(
                method = "POST",
                path = "/v1/reconciliation/report",
                summary = "Reconciliation Settlement Report",
                desc = "Downloads automated 3-way settlement & MDR fee ledger report for GST audits."
            )
        }
    }
}

@Composable
fun ApiEndpointCard(method: String, path: String, summary: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (method == "POST") EmeraldSoftBg else Slate100
                ) {
                    Text(
                        method,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (method == "POST") EmeraldSuccess else NavyPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(path, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Slate900)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(summary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Slate900)
            Text(desc, fontSize = 11.sp, color = Slate700)
        }
    }
}

@Composable
fun MobileSdksTab() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Client Mobile Checkout SDKs", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Native Android & iOS Drop-in Checkout flow", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Android Kotlin Integration", color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        """
val options = PaySetuCheckoutOptions.Builder()
    .setOrderId("ord_781923")
    .setAmount(450.00)
    .setPayeeVpa("sharmakirana@paysetu")
    .setThemeColor("#0A2540")
    .build()

PaySetuCheckout.open(activity, options) { result ->
    when (result) {
        is PaySetuResult.Success -> {
            // Send UTR to your backend for verification!
            backendVerify(result.utr, result.paymentId)
        }
        is PaySetuResult.Failed -> showToast(result.error)
    }
}
                        """.trimIndent(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("iOS Swift SDK Integration", color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        """
let checkout = PaySetuSDK.shared
checkout.startPayment(
    orderId: "ord_781923",
    amount: 450.00,
    merchantVpa: "sharmakirana@paysetu",
    presentingVC: self
) { result in
    switch result {
    case .success(let payment):
        self.verifyOnServer(payment.utr)
    case .failure(let error):
        self.showAlert(error.localizedDescription)
    }
}
                        """.trimIndent(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun WebhookHmacTab() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Webhook HMAC-SHA256 Signature Verification", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Text("Verify incoming event payloads with your merchant secret before updating order state", fontSize = 11.sp, color = Slate400)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Node.js / Express Example", color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        """
const crypto = require('crypto');

app.post('/webhook', (req, res) => {
    const signature = req.headers['x-paysetu-signature'];
    const secret = process.env.PAYSETU_WEBHOOK_SECRET;

    const computed = crypto
        .createHmac('sha256', secret)
        .update(JSON.stringify(req.body))
        .digest('hex');

    if (computed !== signature) {
        return res.status(401).send('Invalid signature');
    }

    // Process event
    if (req.body.event === 'payment.success') {
        markOrderPaid(req.body.orderId, req.body.utr);
    }
    res.status(200).send('OK');
});
                        """.trimIndent(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun ArchitectureTab() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("High-Level Scalable Architecture", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Payment Flow Pipeline", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        """
Mobile Apps (Android & iOS)
   ↓ (TLS 1.3 / Device Fingerprint Binding)
API Gateway (Rate Limiting & Idempotency)
   ↓
Authentication & Role-Based Access Control
   ↓
Payment Orchestration Service
   ├── Risk & Fraud Detection Engine (Velocity, KYC Limits)
   ├── Immutable Double-Entry Ledger Service
   └── Authorised PSP Bank Gateway (HDFC / ICICI / SBI)
         ↓
NPCI UPI Payment Settlement Switch
   ↓
Automated 3-Way Reconciliation & Webhook Dispatch
                        """.trimIndent(),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Slate700
                    )
                }
            }
        }
    }
}
