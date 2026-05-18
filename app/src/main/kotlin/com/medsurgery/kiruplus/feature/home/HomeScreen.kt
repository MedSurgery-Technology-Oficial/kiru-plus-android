package com.medsurgery.kiruplus.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsurgery.kiruplus.R

/**
 * Home dashboard. E0: placeholder con disclaimer banner permanente y navegación stub.
 * E5 reemplazará el placeholder por cards reales (perlas, quick actions, K-Tools).
 */
@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onOpenPaywall: () -> Unit,
    onOpenStore: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { DisclaimerBanner() }
            item { PlaceholderCard("Tab Home", "E5 — perlas del día, accesos rápidos, K-Tools.") }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Navegación stub", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = onOpenProfile) { Text(stringResource(R.string.tab_profile)) }
                        TextButton(onClick = onOpenStore) { Text(stringResource(R.string.tab_store)) }
                        TextButton(onClick = onOpenPaywall) { Text("Premium / Paywall (E7)") }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisclaimerBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Text(
            text = stringResource(R.string.disclaimer_short_banner),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
private fun PlaceholderCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
