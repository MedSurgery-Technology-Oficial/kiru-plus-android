package com.medsurgery.kiruplus.feature.paywall

import androidx.compose.runtime.Composable
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions

@Composable
fun PaywallScreen(onDismiss: () -> Unit) {
    PaywallDialog(
        PaywallDialogOptions.Builder()
            .setDismissRequest(onDismiss)
            .build(),
    )
}
