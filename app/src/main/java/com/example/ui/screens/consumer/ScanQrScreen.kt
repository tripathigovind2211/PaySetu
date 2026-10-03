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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BharatSaffron
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onQrScanned: (payeeName: String, payeeVpa: String, amount: Double, note: String) -> Unit
) {
    var manualVpa by remember { mutableStateOf("") }
    var manualAmount by remember { mutableStateOf("") }
    var manualNote by remember { mutableStateOf("") }

    val presetQrs = listOf(
        Triple("Sharma Supermarket & Kirana", "sharmakirana@paysetu", 420.0),
        Triple("Swiggy India (Bundl Technologies)", "swiggy@icici", 385.0),
        Triple("Tata Power Delhi Electricity", "tatapower@axisbank", 1250.0),
        Triple("Indian Oil Fuel Station", "indianoil@sbi", 850.0),
        Triple("Blue Tokai Coffee Roasters", "bluetokai@yesbank", 295.0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .testTag("scan_qr_screen")
            .verticalScroll(rememberScrollState())
    ) {
        // Top Nav
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_back_scan")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Scan Any Bharat/UPI QR",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // Viewfinder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp)
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .border(2.dp, EmeraldLight, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Align UPI QR code inside the frame",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Supports BHIM, PhonePe, Paytm, GPay & BharatQR",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }
        }

        // Quick Preset QRs Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Simulate Scanning Verified Merchant QRs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Slate900
                )
                Text(
                    "Tap any merchant below to test instant UPI QR checkout",
                    fontSize = 11.sp,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(10.dp))

                presetQrs.forEach { (name, vpa, amount) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onQrScanned(name, vpa, amount, "QR Payment")
                            }
                            .testTag("preset_qr_${vpa.substringBefore('@')}"),
                        shape = RoundedCornerShape(10.dp),
                        color = Slate100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Store,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                                    Text(vpa, fontSize = 11.sp, color = Slate400)
                                }
                            }
                            Text(
                                "₹${String.format("%.2f", amount)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }
        }

        // Manual UPI ID Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Or Pay via Virtual Payment Address (VPA)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = manualVpa,
                    onValueChange = { manualVpa = it },
                    label = { Text("Enter UPI ID (e.g. friend@upi)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_manual_vpa"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = manualAmount,
                    onValueChange = { manualAmount = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_manual_amount"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val amt = manualAmount.toDoubleOrNull() ?: 100.0
                        val name = manualVpa.substringBefore("@").replaceFirstChar { it.uppercase() }
                        onQrScanned(name, manualVpa, amt, "Direct VPA Transfer")
                    },
                    enabled = manualVpa.contains("@") && (manualAmount.toDoubleOrNull() ?: 0.0) > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_proceed_manual_pay"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Proceed to Pay", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
