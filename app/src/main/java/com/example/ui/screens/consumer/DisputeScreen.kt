package com.example.ui.screens.consumer

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DisputeEntity
import com.example.data.local.entities.PaymentEntity
import com.example.domain.model.DisputeStatus
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberReward
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun DisputeScreen(
    disputes: List<DisputeEntity>,
    targetPayment: PaymentEntity?,
    onBack: () -> Unit,
    onSubmitDispute: (paymentId: String, category: String, remarks: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("UNAUTHORIZED_DEBIT") }
    var userRemarks by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val categories = listOf(
        "UNAUTHORIZED_DEBIT" to "Account debited but beneficiary did not receive funds",
        "INCORRECT_AMOUNT" to "Wrong amount charged by merchant",
        "MERCHANT_SERVICE_ISSUE" to "Merchant failed to deliver goods or service",
        "DUPLICATE_DEBIT" to "Payment debited multiple times for a single order"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
            .testTag("dispute_screen")
            .padding(16.dp)
    ) {
        // Nav Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_dispute")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate900)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Payment Dispute & Grievance (ODR)", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900)
                    Text("NPCI Online Dispute Resolution & RBI Ombudsman Framework", fontSize = 11.sp, color = Slate400)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // New Dispute Form if target payment provided
        if (targetPayment != null && !isSubmitted) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Raise Dispute on Payment", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Payment ID: ${targetPayment.paymentId} • UTR: ${targetPayment.transactionRef}",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                        Text(
                            "Paid: ₹${String.format("%.2f", targetPayment.amount)} to ${targetPayment.payeeName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Select Dispute Ground", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))

                        categories.forEach { (catKey, catDesc) ->
                            val isSelected = selectedCategory == catKey
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { selectedCategory = catKey },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EmeraldSoftBg else Slate100,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldSuccess else CardBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        catKey.replace("_", " "),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) EmeraldSuccess else Slate900
                                    )
                                    Text(catDesc, fontSize = 11.sp, color = Slate700)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = userRemarks,
                            onValueChange = { userRemarks = it },
                            label = { Text("Details & Description of Issue") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_dispute_remarks"),
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onSubmitDispute(targetPayment.paymentId, selectedCategory, userRemarks.ifBlank { "Customer filed dispute via in-app ODR." })
                                isSubmitted = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_dispute"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit to NPCI ODR Portal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Active / Previous Disputes List
        item {
            Text("Filed Disputes & Resolution Status", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (disputes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No active disputes filed.", color = Slate400, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(disputes) { dispute ->
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
                            Text(
                                dispute.disputeRefNo,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = NavyPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (dispute.status) {
                                    DisputeStatus.OPEN -> AmberLight
                                    DisputeStatus.UNDER_INVESTIGATION -> Slate100
                                    DisputeStatus.RESOLVED_REFUND -> EmeraldSoftBg
                                    DisputeStatus.REJECTED -> Color(0xFFFEE2E2)
                                }
                            ) {
                                Text(
                                    dispute.status.name.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (dispute.status) {
                                        DisputeStatus.OPEN -> AmberReward
                                        DisputeStatus.UNDER_INVESTIGATION -> Slate700
                                        DisputeStatus.RESOLVED_REFUND -> EmeraldSuccess
                                        DisputeStatus.REJECTED -> Color(0xFFDC2626)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Ground: ${dispute.disputeCategory.replace("_", " ")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate900
                        )
                        Text(
                            "UTR: ${dispute.transactionRef}",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Customer Remarks: ${dispute.userRemarks}",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
