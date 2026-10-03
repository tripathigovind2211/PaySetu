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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.local.entities.PaymentEntity
import com.example.domain.model.TransactionState
import com.example.ui.components.TransactionStateChip
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberReward
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
fun PaymentReceiptScreen(
    payment: PaymentEntity,
    onDoneClick: () -> Unit,
    onRaiseDisputeClick: (PaymentEntity) -> Unit
) {
    var isScratchCardRevealed by remember { mutableStateOf(false) }
    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        .format(Date(payment.timestamp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
            .testTag("payment_receipt_screen")
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Success / Status Badge
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    if (payment.status == TransactionState.SUCCESS) EmeraldSoftBg else CrimsonSoftBg
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (payment.status == TransactionState.SUCCESS) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (payment.status == TransactionState.SUCCESS) EmeraldSuccess else CrimsonDanger,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (payment.status == TransactionState.SUCCESS) "Paid Successfully" else "Payment ${payment.status.name}",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900
        )
        Text(
            "₹${String.format("%.2f", payment.amount)}",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = NavyPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Receipt Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("receipt_details_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ReceiptRow("Payee", payment.payeeName, isBold = true)
                ReceiptRow("Payee UPI ID", payment.payeeVpa)
                ReceiptRow("Payer Account", "HDFC Bank (•••• 1089)")
                ReceiptRow("UPI UTR / Bank RRN", payment.transactionRef, isMono = true)
                ReceiptRow("Payment Mode", payment.mode.name.replace("_", " "))
                ReceiptRow("Date & Time", formattedDate)

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)

                ReceiptRow("Order Value", "₹${String.format("%.2f", payment.amount)}")
                ReceiptRow("Platform Convenience Fee", "₹0.00")
                if (payment.cashback > 0) {
                    ReceiptRow("Cashback Applied", "- ₹${String.format("%.2f", payment.cashback)}", textColor = AmberReward)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Transaction State", fontSize = 13.sp, color = Slate700)
                    TransactionStateChip(state = payment.status)
                }
            }
        }

        // Promotional Cashback Scratchcard (if eligible)
        if (payment.status == TransactionState.SUCCESS && payment.cashback > 0) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isScratchCardRevealed = true }
                    .testTag("cashback_scratchcard"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isScratchCardRevealed) AmberLight else AmberReward
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isScratchCardRevealed) {
                        Icon(Icons.Default.Redeem, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("You've Won a Merchant Reward!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Tap to scratch and reveal cashback", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    } else {
                        Text("🎉 Cashback Credited to Bank Account", color = AmberReward, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹${String.format("%.2f", payment.cashback)}",
                            color = Slate900,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp
                        )
                        Text("Legitimate merchant-funded promotional reward", color = Slate700, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { onRaiseDisputeClick(payment) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_raise_dispute"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Dispute / ODR", fontSize = 12.sp)
            }

            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_receipt_done"),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isMono: Boolean = false,
    textColor: Color = Slate900
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Slate700)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            color = textColor
        )
    }
}
