package com.medsurgery.kiruplus.feature.academy

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.feature.main.TabPlaceholderBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_academy)) }) },
    ) { padding ->
        TabPlaceholderBody(
            modifier = androidx.compose.ui.Modifier,
            paddingValues = padding,
            titleRes = R.string.home_card_academy_title,
            bodyRes = R.string.home_card_academy_body,
        )
    }
}
