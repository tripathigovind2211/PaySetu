package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.PaySetuDatabase
import com.example.data.repository.PaymentRepository
import com.example.ui.PaySetuApp
import com.example.ui.PaymentViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = PaySetuDatabase.getInstance(applicationContext)
        val repository = PaymentRepository(database)
        val factory = PaymentViewModel.provideFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]

        // Handle incoming UPI Intent deep link (e.g., upi://pay?pa=...&pn=...&am=...)
        handleIncomingUpiIntent(intent)

        setContent {
            MyApplicationTheme {
                PaySetuApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingUpiIntent(intent)
    }

    private fun handleIncomingUpiIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme?.lowercase() == "upi" && uri.host?.lowercase() == "pay") {
            val payload = viewModel.repository.pspBridgeService.parseUpiUri(uri.toString())
            if (payload != null) {
                viewModel.startCheckout(
                    payeeName = payload.payeeName,
                    payeeVpa = payload.payeeVpa,
                    amount = payload.amount ?: 100.0,
                    note = payload.transactionNote ?: "UPI Online Merchant Checkout",
                    orderId = payload.transactionRef
                )
            }
        }
    }
}
