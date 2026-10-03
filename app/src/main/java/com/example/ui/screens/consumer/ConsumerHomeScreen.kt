package com.example.ui.screens.consumer

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.BankAccountEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.UserEntity
import com.example.domain.model.KycStatus
import com.example.ui.components.TransactionStateChip
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberReward
import com.example.ui.theme.BharatSaffron
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
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
fun ConsumerHomeScreen(
    user: UserEntity?,
    bankAccounts: List<BankAccountEntity>,
    payments: List<PaymentEntity>,
    totalCashback: Double,
    onScanQrClick: () -> Unit,
    onPayVpaClick: () -> Unit,
    onKycClick: () -> Unit,
    onDisputeClick: (PaymentEntity) -> Unit,
    onPaymentClick: (PaymentEntity) -> Unit
) {
    val primaryAccount = bankAccounts.firstOrNull { it.isPrimary } ?: bankAccounts.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("consumer_home_screen")
            .padding(horizontal = 16.dp)
    ) {
        // Linked UPI Bank Account Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("bank_account_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NavyDark, NavyPrimary, Color(0xFF0F3460))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        primaryAccount?.bankName ?: "HDFC Bank",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        primaryAccount?.maskedAccountNumber ?: "•••• 1089",
                                        color = Slate400,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // KYC badge
                            val isFullKyc = user?.kycStatus == KycStatus.FULL_KYC
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isFullKyc) EmeraldSuccess.copy(alpha = 0.25f) else BharatSaffron.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isFullKyc) EmeraldLight else BharatSaffron
                                ),
                                modifier = Modifier.clickable { onKycClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = if (isFullKyc) EmeraldLight else BharatSaffron,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (isFullKyc) "Full KYC" else "Min KYC",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Primary UPI ID (VPA)", color = Slate400, fontSize = 11.sp)
                                Text(
                                    primaryAccount?.upiVpa ?: "rohit@paysetu",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                "IFSC: ${primaryAccount?.ifscCode ?: "HDFC0001234"}",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Payment Actions Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Direct UPI Money Movement",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Scan QR",
                            color = EmeraldSuccess,
                            testTag = "btn_scan_qr",
                            onClick = onScanQrClick
                        )
                        QuickActionItem(
                            icon = Icons.Default.PhoneAndroid,
                            label = "Pay UPI ID",
                            color = NavyPrimary,
                            testTag = "btn_pay_vpa",
                            onClick = onPayVpaClick
                        )
                        QuickActionItem(
                            icon = Icons.Default.ElectricBolt,
                            label = "Pay Bills",
                            color = BharatSaffron,
                            testTag = "btn_pay_bills",
                            onClick = onPayVpaClick
                        )
                        QuickActionItem(
                            icon = Icons.Default.VerifiedUser,
                            label = "KYC Hub",
                            color = Color(0xFF6B21A8),
                            testTag = "btn_kyc_hub",
                            onClick = onKycClick
                        )
                    }
                }
            }
        }

        // Cashback Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = AmberLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberReward.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmberReward),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Total Cashback Earned",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AmberReward
                        )
                        Text(
                            "₹${String.format("%.2f", totalCashback)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                    }
                    Text(
                        "Merchant-funded",
                        fontSize = 10.sp,
                        color = Slate700,
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Recent Payments Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Transactions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    "${payments.size} records",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }

        // Transactions List
        if (payments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Slate400, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No payment records yet", color = Slate700, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(payments) { payment ->
                TransactionCardItem(
                    payment = payment,
                    onClick = { onPaymentClick(payment) },
                    onDispute = { onDisputeClick(payment) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Slate900
        )
    }
}

@Composable
fun TransactionCardItem(
    payment: PaymentEntity,
    onClick: () -> Unit,
    onDispute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .testTag("tx_card_${payment.paymentId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(EmeraldSoftBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        payment.payeeName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        payment.payeeName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900,
                        maxLines = 1
                    )
                    Text(
                        payment.payeeVpa,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Text(
                        "UTR: ${payment.transactionRef}",
                        fontSize = 10.sp,
                        color = Slate700
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%.2f", payment.amount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(4.dp))
                TransactionStateChip(state = payment.status)
            }
        }
    }
}
