package com.example.ui.screens.consumer

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserEntity
import com.example.domain.model.KycStatus
import com.example.ui.theme.BharatSaffron
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
fun KycScreen(
    user: UserEntity?,
    onBack: () -> Unit,
    onUpgradeKyc: () -> Unit
) {
    val isFullKyc = user?.kycStatus == KycStatus.FULL_KYC

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
            .testTag("kyc_screen")
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Nav Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_kyc")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate900)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("KYC & Compliance Verification", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900)
                Text("RBI Master Directions on Prepaid Instruments & Payments", fontSize = 11.sp, color = Slate400)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFullKyc) EmeraldSoftBg else Color(0xFFFFF7ED)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isFullKyc) EmeraldSuccess else BharatSaffron
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isFullKyc) EmeraldSuccess else BharatSaffron),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFullKyc) Icons.Default.CheckCircle else Icons.Default.Security,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        if (isFullKyc) "Full Video-KYC Approved" else "Minimum KYC (Tier 1)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Slate900
                    )
                    Text(
                        if (isFullKyc)
                            "Limits unlocked: Up to ₹1,00,000 per transaction / ₹5,00,000 per month."
                        else
                            "Monthly transaction cap: ₹10,000. Upgrade to Video-KYC for higher limits.",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Regulated Documents (Masked Storage Compliance)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Government Identified Documents", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                Text("Stored under Digital Personal Data Protection (DPDP) Act with zero plain-text leaks", fontSize = 11.sp, color = Slate400)

                Spacer(modifier = Modifier.height(14.dp))

                KycDocRow(
                    icon = Icons.Default.Badge,
                    title = "Permanent Account Number (PAN)",
                    value = user?.maskedPan?.ifBlank { "ABCDE****F" } ?: "ABCDE****F",
                    status = "VERIFIED with NSDL"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)

                KycDocRow(
                    icon = Icons.Default.Lock,
                    title = "Aadhaar Card (UIDAI)",
                    value = user?.maskedAadhaar?.ifBlank { "XXXX-XXXX-4921" } ?: "XXXX-XXXX-4921",
                    status = if (isFullKyc) "VERIFIED via OTP" else "PENDING E-KYC"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upgrade Video KYC Prompt
        if (!isFullKyc) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Upgrade to Full Video-KYC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Complete RBI-mandated V-CIP (Video based Customer Identification Process) with a certified bank officer in 2 minutes.",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onUpgradeKyc,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_complete_video_kyc"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Simulate Video-KYC Verification", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun KycDocRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate900)
                Text(value, fontSize = 12.sp, color = Slate700)
            }
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = EmeraldSoftBg
        ) {
            Text(
                status,
                color = EmeraldSuccess,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
