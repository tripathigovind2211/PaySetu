package com.example.ui.screens.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.BankAccountEntity
import com.example.ui.ActiveCheckoutState
import com.example.ui.components.ItemizedFeeCard
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

@Composable
fun PaymentCheckoutDialog(
    state: ActiveCheckoutState,
    bankAccounts: List<BankAccountEntity>,
    onDismiss: () -> Unit,
    onAuthorizePayment: () -> Unit
) {
    var selectedAccountIndex by remember { mutableStateOf(0) }
    val selectedAccount = bankAccounts.getOrNull(selectedAccountIndex)
        ?: bankAccounts.firstOrNull()

    Dialog(onDismissRequest = { if (!state.isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("payment_checkout_sheet"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Sheet Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(NavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("₹", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "NPCI Authorized UPI Checkout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                    }
                    if (!state.isProcessing) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payee Details Header
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Paying To", fontSize = 11.sp, color = Slate400)
                        Text(
                            state.payeeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Slate900
                        )
                        Text(
                            "UPI VPA: ${state.payeeVpa}",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Itemized Breakdown (Zero Hidden Fees)
                state.calculation?.let { calc ->
                    ItemizedFeeCard(calc = calc)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Debiting Bank Selector
                Text(
                    "Pay From Linked Bank Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(6.dp))

                bankAccounts.forEachIndexed { index, account ->
                    val isChosen = selectedAccountIndex == index
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { if (!state.isProcessing) selectedAccountIndex = index }
                            .testTag("select_account_${account.accountId}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isChosen) EmeraldSoftBg else Slate100,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isChosen) EmeraldSuccess else CardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isChosen) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isChosen) EmeraldSuccess else Slate400,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        account.bankName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Slate900
                                    )
                                    Text(
                                        "${account.maskedAccountNumber} • UPI ID: ${account.upiVpa}",
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Notice if any
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CrimsonSoftBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonDanger.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonDanger, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                state.errorMessage,
                                color = CrimsonDanger,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Authorize Payment Button (Biometric / Authorised PSP Flow)
                if (state.isProcessing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = EmeraldSuccess,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Connecting to Authorised PSP Gateway...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700
                        )
                        Text(
                            "Verifying device signature and ledger idempotency",
                            fontSize = 10.sp,
                            color = Slate400
                        )
                    }
                } else {
                    Button(
                        onClick = onAuthorizePayment,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_authorize_biometric_payment"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Authorize ₹${String.format("%.2f", state.calculation?.finalPayableAmount ?: state.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Zero PIN Storage Guarantee: Sensitive PIN/passwords are never captured by PaySetu.",
                        fontSize = 9.sp,
                        color = Slate400,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
