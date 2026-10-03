package com.example.ui.components

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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.EnvironmentMode
import com.example.domain.model.RevenueCalculation
import com.example.domain.model.RiskLevel
import com.example.domain.model.TransactionState
import com.example.domain.model.UserRole
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberReward
import com.example.ui.theme.BharatSaffron
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CrimsonSoftBg
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun PaySetuHeader(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    envMode: EnvironmentMode,
    onEnvModeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("paysetu_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .background(EmeraldSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("₹", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "PaySetu",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "UPI",
                                color = BharatSaffron,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            "NPCI / RBI PSP Bridge",
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    }
                }

                // Environment Chip
                val envColor = when (envMode) {
                    EnvironmentMode.DEMO -> EmeraldLight
                    EnvironmentMode.SANDBOX -> BharatSaffron
                    EnvironmentMode.PRODUCTION -> CrimsonDanger
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = envColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, envColor),
                    modifier = Modifier
                        .clickable { onEnvModeToggle() }
                        .testTag("env_mode_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(envColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = envMode.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Role Navigation Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserRole.values().forEach { role ->
                    val isSelected = currentRole == role
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clickable { onRoleSelected(role) }
                            .testTag("role_tab_${role.name.lowercase()}"),
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (role) {
                                    UserRole.CONSUMER -> "Consumer"
                                    UserRole.MERCHANT -> "Merchant"
                                    UserRole.ADMIN -> "Admin"
                                    UserRole.DEVELOPER -> "API/Docs"
                                },
                                color = if (isSelected) NavyPrimary else Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionStateChip(state: TransactionState) {
    val (bgColor, textColor, icon) = when (state) {
        TransactionState.SUCCESS -> Triple(EmeraldSoftBg, EmeraldSuccess, Icons.Default.CheckCircle)
        TransactionState.FAILED, TransactionState.EXPIRED -> Triple(CrimsonSoftBg, CrimsonDanger, Icons.Default.Error)
        TransactionState.REFUND_INITIATED, TransactionState.REFUNDED -> Triple(Slate100, Slate800, Icons.Default.Payment)
        TransactionState.DISPUTED -> Triple(AmberLight, AmberReward, Icons.Default.Warning)
        else -> Triple(Slate100, Slate700, Icons.Default.HourglassTop)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = state.name.replace("_", " "),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RiskChip(level: RiskLevel) {
    val color = when (level) {
        RiskLevel.LOW -> EmeraldSuccess
        RiskLevel.MEDIUM -> AmberReward
        RiskLevel.HIGH, RiskLevel.BLOCKED -> CrimsonDanger
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = "Risk: ${level.name}",
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ItemizedFeeCard(calc: RevenueCalculation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("itemized_fee_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order Amount", fontSize = 13.sp, color = Slate700)
                Text("₹${String.format("%.2f", calc.transactionAmount)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Convenience Fee", fontSize = 13.sp, color = Slate700)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("₹0.00", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(Zero fee)", fontSize = 11.sp, color = EmeraldSuccess)
                }
            }

            if (calc.cashback > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cashback Reward", fontSize = 13.sp, color = AmberReward)
                    Text("- ₹${String.format("%.2f", calc.cashback)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberReward)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Slate200
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Text("Inclusive of all taxes", fontSize = 10.sp, color = Slate400)
                }
                Text(
                    "₹${String.format("%.2f", calc.finalPayableAmount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Regulated Indian Banking Protocol • No hidden charges",
                    fontSize = 10.sp,
                    color = Slate700
                )
            }
        }
    }
}
